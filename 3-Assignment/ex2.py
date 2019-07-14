#!/usr/bin/python3

import os
import logging
logging.basicConfig(level=logging.INFO)

def collect_sources(root, sources, followlinks=False):
  """


  """

  if not os.path.isdir(root):
    logging.info("Argument root is not a directory")
    return

  if type(followlinks) not bool:
    followlinks = False
    logging.info("Invalid followlinks value, setting to False")

  valid_sources = []
  valid_ext = [".java", ".hs", ".py"]

  for root, dirs, files in os.walk(root, followlinks=followlinks):
    for filename in files:
      (filename_root, filename_ext) = os.path.splitext(filename)
      if filename_ext in valid_ext:
        source = os.path.join(root, filename)
        valid_sources.append(source)
        logging.info("Added {sources} to valid sources")

  sources_path = os.path.join(root, sources)

  try:
    sources_file = open(sources_path, "w")
  except Exception as ex:
    logging.info("Exception opening sources file {sources_path}")
    logging.info("Error: {ex}")
    return

  try:
    for valid_source in valid_sources:
      sources_file.writeline(valid_source)
  except Exception as ex:
    logging.info("Exception in writing sources file")
    logging.info("Error: {ex}")

  sources_file.close()
