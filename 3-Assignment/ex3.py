#!/usr/bin/python3

import re
import os
import logging
logging.basicConfig(level=logging.INFO)

def get_package(path):
  """

  """

  package_regex = r"package\s+([a-zA_Z_][\.\w]*);"
  package = []

  try:
    file = open(path, "r")
    file_content = file.read()
    file.close()
  except Exception as ex:
    logging.info("Exception Cannot read file {file}")
    logging.info("Error: {ex}")

  packages = re.findall(package_regex, file_content)

  if len(packages) > 0:
    package = packages[0].split(".")

  return package

def rebuild_package(root, filename):
  """

  """

  path = os.path.join(root, filename)
  package = get_package(path)

def rebuild_packages(root, followlinks=False):
  """

  """

  if not os.path.isdir(root):
    logging.info("Argument root is not a directory")
    return

  if type(followlinks) not bool:
    followlinks = False
    logging.info("Invalid followlinks value, setting to False")

  for root, dirs, files in os.walk(root, followlinks=followlinks):
    for filename in files:
      (filename_root, filename_ext) = os.path.splitext(filename)
      if filename_ext == ".java":
        rebuild_package(root, filename)
