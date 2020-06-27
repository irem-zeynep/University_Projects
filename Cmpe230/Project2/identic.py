import argparse
import os
import hashlib
from collections import OrderedDict
import io

#Irem Zeynep Alagoz 2018400063
# Gets absolute path of given path, gives error if path is not valid.
def get_abs(d_path):
    abs_path = os.path.abspath(os.path.expanduser(d_path))
    if os.path.exists(d_path):
        return abs_path
    else:
        raise SyntaxError('Given directory is invalid')


# Hashes and returns file content. Reads file block by block to calculates its hash code.
def hash_file(f_path):
    hf = hashlib.sha256()
    block_size = 1024*1024*10  # Size of each block
    with io.open(f_path, 'rb') as file:
        block = file.read(block_size)
        while len(block) != 0:  # If no data left stops reading
            hf.update(block)
            block = file.read(block_size)
    return hf.hexdigest()


# Sorts hash codes of children before concatenation, hashes the concatenation again and returns it.
def hash_parent(child_hash, parent_hash):
    for child_key in sorted(child_hash):
        parent_hash += child_key
    return hashlib.sha256(parent_hash.encode('utf8')).hexdigest()


# Updates dictionary.
def update_dict(key, value):
    if key in hash_dict:
        hash_dict[key].add(value)
    else:
        hash_dict[key] = {value}


# Reads from terminal and give flags true or false value to guide the program.
parser = argparse.ArgumentParser(prog='identic',
                                 description='Traverses given directories and prints full pathname of '
                                             'files or directories that are identical. Meaning of identical changes'
                                             'according to given arguments')
# Since -f and -d options can not be used together they are in mutually exclusive group1.
group1 = parser.add_mutually_exclusive_group()
group1.add_argument('-f', dest='file', help='Searches on files', action='store_true', default=False)
group1.add_argument('-d', dest='directory', help='Searches on directories', action='store_true', default=False)

parser.add_argument('-c', dest='content', help='Searches same content', action='store_true', default=False)
parser.add_argument('-n', dest='name', help='Searches same name', action='store_true', default=False)
parser.add_argument('-s', dest='size', help='Prints size', action='store_true', default=False)
parser.add_argument('given_directory', nargs='*')
args = parser.parse_args()

args.content = args.content or (not args.name)  # Default flag is content.
args.size = args.size and args.content  # Size flag can be true only if content flag is also true.
# Sorts to bring parent directories to the top. Default value is current directory.
directory_list = sorted(args.given_directory) or [os.getcwd()]
hash_dict = {}  # {hash:path}
directories = [get_abs(directory_list[0])]  # Holds filtered absolute paths of given directory list.
# Gets rid of the subdirectories to avoid recalculation.
for i in range(1, len(directory_list)):
    directory_path = get_abs(directory_list[i])  # Gets absolute path.
    if not os.path.commonpath([directory_path, directories[-1]]) == directories[-1]:
        directories.append(directory_path)

if args.directory:
    # {path: list of hash values} holds hash value of calculated children, prevents recalculation.
    # list of hash values may contain name, content and size based on given arguments.
    child_directory = {}  
    for directory in directories:
        for root, dirs, files in os.walk(directory, topdown=False):
            # Resets before starting calculation of new dictionary
            size = 0
            hash_name, hash_content = '', ''    # their concatenation becomes key in hash_dict.
            # list that holds hash values and size of current directory.
            parent = []
            # Lists that holds hash of children.
            contents = []
            names = []
            for file_name in files:
                file_path = os.path.join(root, file_name)  # Gets absolute path.
                if args.content:
                    contents.append(hash_file(file_path))
                if args.name:
                    names.append(hashlib.sha256(file_name.encode('utf8')).hexdigest())
                if args.size:
                    size += os.path.getsize(file_path)  # Updates size
            for dir_name in dirs:
                dir_path = os.path.join(root, dir_name)  # Gets absolute path.
                child = child_directory[dir_path]
                # Since size depends on the flags and indexes of hash values are unknown, pop() is preferred.
                if args.size:
                    size += child.pop()  # Updates size by getting size of child directory.
                if args.content:
                    contents.append(child.pop())
                if args.name:
                    names.append(child.pop())
                del child_directory[dir_path]  # Deletes to reduce space complexity.

            if args.name:  # If name flag is true, concatenates hash of directory's name.                
                hash_name = hash_parent(names, hashlib.sha256(os.path.basename(root).encode('utf8')).hexdigest())
                parent.append(hash_name)
            if args.content:
                hash_content = hash_parent(contents, '')
                parent.append(hash_content)
            # Updates the dictionary and the list
            if args.size:
                update_dict((hash_name + hash_content, size), root)
                parent.append(size)
                child_directory[root] = parent
            else:
                update_dict(hash_name + hash_content, root)
                child_directory[root] = parent
# Else, for every directory in dir_list, iterates every file in the directory recursively.
else:
    for directory in directories:
        for root, dirs, files in os.walk(directory):
            for file_name in files:
                size = 0
                # key of hash dictionary.
                hash_value = ''
                file_path = os.path.join(root, file_name)
                if args.content:
                    hash_value += hash_file(file_path)
                if args.name:
                    hash_value += hashlib.sha256(file_name.encode('utf8')).hexdigest()
                # If size is needed uses (hash_value, size) tuple as key
                if args.size:
                    update_dict((hash_value, os.path.getsize(file_path)), file_path)
                else:
                    update_dict(hash_value, file_path)

sorted_sets = {}
# Sorts each duplicate set alphabetically in itself.
for hash_key in hash_dict:
    same_hash = hash_dict[hash_key]
    if len(same_hash) > 1:
        sorted_sets[hash_key] = sorted(same_hash)
# If size flag is true, prints according to size in decreasing order
if args.size:
    sorted_dict = OrderedDict(sorted(sorted_sets.items(), key=lambda kv: (-kv[0][-1], kv[1][0])))
    for hash_key in sorted_dict:
        for path in sorted_dict[hash_key]:
            print(path + '\t' + str(hash_key[-1]))
        print()
# Else, prints in lexicographic order
else:
    sorted_dict = OrderedDict(sorted(sorted_sets.items(), key=lambda kv: kv[1][0]))
    for hash_key in sorted_dict:
        for path in sorted_dict[hash_key]:
            print(path)
        print()
