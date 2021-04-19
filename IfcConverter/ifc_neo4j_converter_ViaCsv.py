import ifcopenshell
import sys
import time
import csv
import itertools
import copy
import os
import stat
import subprocess


def typeDict(key):
    f = ifcopenshell.file()
    value = f.create_entity(key).wrapped_data.get_attribute_names()
    return value


def dict_key_setter(dict_list):
    dict_keys = list(set(itertools.chain.from_iterable([i.keys() for i in dict_list])))
    for d in dict_list:
        for k in dict_keys:
            d.setdefault(k, None)
    return dict_list, dict_keys


def change_dict_key(d, old_key, new_key):
    if old_key in d:
        d[new_key] = d.pop(old_key)


start = time.time()  # Culculate time to process
print("Start!")
print(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime())))
log1 = str(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime()))) + " Start "

####################################################
# IFC path
####################################################

# ifc_path = "ifc_files/IfcOpenHouse_original.ifc"
# ifc_path = "ifc_files/191225_TE-Bld_zone_GEO.ifc"
ifc_path = "ifc_files/231110AC11-Institute-Var-2-IFC.ifc"

####################################################
# neo4j root path
####################################################

path_ne4j_root = 'C:/Users/adams/.Neo4jDesktop/relate-data/dbmss/dbms-302c3856-1f2c-4e22-98ac-2a09f093e1ef'

####################################################
# Nodes and Edges list create
####################################################
nodes = []
edges = []
f = ifcopenshell.open(ifc_path)

for el in f:
    if el.is_a() == "IfcOwnerHistory":
        continue
    tid = el.id()
    cls = el.is_a()
    node = {"nid:ID": tid, ":LABEL": cls}
    keys = []
    try:
        keys = [x for x in el.get_info() if x not in ["type", "id", "OwnerHistory"]]
    except RuntimeError:
        # we actually can't catch this, but try anyway
        pass
    for key in keys:
        val = el.get_info()[key]
        if any(hasattr(val, "is_a") and val.is_a(thisTyp)
               for thisTyp in ["IfcBoolean", "IfcLabel", "IfcText", "IfcReal"]):
            val = val.wrappedValue
        if val and type(val) is tuple and type(val[0]) in (str, bool, float, int):
            val = ",".join(str(x) for x in val)
        if type(val) not in (str, bool, float, int):
            continue
        node[key] = val
    nodes.append(node)

    for i in range(len(el)):
        try:
            el[i]
            typeDict(cls)[i]
        except RuntimeError as e:
            if str(e) != "Entity not found":
                print("ID", tid, e, file=sys.stderr)
            continue
        if isinstance(el[i], ifcopenshell.entity_instance):
            if el[i].is_a() == "IfcOwnerHistory":
                continue
            if el[i].id() != 0:
                edges.append([tid, el[i].id(), typeDict(cls)[i]])
                continue
        try:
            iter(el[i])
        except TypeError:
            continue
        destinations = [
            x.id() for x in el[i] if isinstance(
                x, ifcopenshell.entity_instance)]
        for connectedTo in destinations:
            # i dont know why, but "trim" reltype sometimes have connectedto"0"
            if connectedTo == 0:
                continue
            edges.append([tid, connectedTo, typeDict(cls)[i]])

if len(nodes) == 0:
    print("no nodes in file", file=sys.stderr)
    sys.exit(1)

####################################################
# Directory setup
####################################################

csv_base_path = path_ne4j_root + "/bin/importer_csv/"

if not os.path.exists(csv_base_path):
    os.mkdir(csv_base_path)

####################################################
# Nodes csv create
####################################################

cls_list = set([i[":LABEL"] for i in nodes])

for s_cls in cls_list:
    dicts_raw = [i for i in nodes if i[":LABEL"] == s_cls]
    dicts, headers = dict_key_setter(dicts_raw)
    headers_raw = copy.copy(headers)
    dicts_mod = []
    for i, header in enumerate(headers):
        val_type = set([str(type(p[header])) for p in dicts])
        if header == "nid:ID" or header == ":LABEL":
            continue
        if header == "Id":
            headers[i] = "ifcID"
            print("id!!!!!!!!")
        if any(
            val_type == s for s in [
                {"<class 'int'>"}, {"<class 'double'>"}, {
                    "<class 'int'>", "<class 'NoneType'>"}, {
                "<class 'double'>", "<class 'NoneType'>"}]):
            headers[i] += ":double"
    for d in dicts:
        for n in range(len(headers)):
            change_dict_key(d, headers_raw[n], headers[n])
            dicts_mod.append(d)
    rows = [[v if v is not None else "" for v in p.values()] for p in dicts]
    csv_path = csv_base_path + s_cls + ".csv"
    with open(csv_path, 'w', newline="", encoding='utf_8_sig') as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(dicts)

####################################################
# Edges csv create
####################################################

with open(csv_base_path + "Edges.csv", 'w', newline="", encoding='utf_8_sig') as f:
    writer = csv.writer(f)
    writer.writerow([":START_ID", ":END_ID", ":TYPE"])
    writer.writerows(edges)

####################################################
# neo4j-import_setting.txt create
####################################################

if os.name == 'nt':
    file_extension = 'bat'
    line_break = '^'
    additional_action = None
    run_cmd = lambda path: subprocess.run([path])
elif os.name == 'posix':
    file_extension = 'sh'
    line_break = '\\'
    additional_action = lambda path: os.chmod(path, stat.S_IEXEC)
    run_cmd = lambda path: subprocess.run([path])
else:
    print('WARNING: Operating system not recognized; generic import command will be generated to .txt file but NO IMPORT SCRIPTS WILL BE RUN')
    file_extension = 'txt'
    line_break = ''
    additional_action = None
    run_cmd = None

file_relative_path = f'/bin/neo4j-import.{file_extension}'
full_path = path_ne4j_root + file_relative_path

with open(full_path, mode="w") as f:
    f.write('cd ' + path_ne4j_root + '/bin/\n')
    f.write('neo4j-admin import ' + line_break + '\n')
    for s_cls in cls_list:
        f.write(" --nodes=importer_csv\\" + s_cls + ".csv" + line_break + '\n')
    f.write(" --relationships=importer_csv\\Edges.csv")

####################################################
# log
####################################################

print("IFC parsing and intermidiate files generation done in ", round(time.time() - start))
print(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime())))
log2 = str(round(time.time() - start)) + "sec.\n" + \
    str(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime()))) + " IFC parsing and intermidiate files generation done"

import_start_timestamp = time.time()

####################################################
# run import
####################################################

if additional_action:
    additional_action(full_path)

if run_cmd:
    run_cmd(full_path)

####################################################
# log
####################################################

print(f'Import done in {round(time.time() - start)} seconds')
print(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime())))
log3 = str(round(time.time() - import_start_timestamp)) + "sec.\n" + \
    str(time.strftime("%Y/%m/%d %H:%M:%S", time.strptime(time.ctime()))) + " Import done"

with open("log.txt", mode="a") as f:
    f.write(ifc_path + "\n")
    f.write("Nodes_" + str(len(nodes)) + " ,Edges_" + str(len(edges)) + "\n")
    f.write(log1 + "\n")
    f.write(log2 + "\n")
    f.write(log3 + '\n')
