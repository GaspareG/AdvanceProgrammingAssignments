#!/usr/bin/python3

import os
import logging
logging.basicConfig(level=logging.INFO)

def raj2jar(root, followlinks=False):
  """
  Given an absolute path of a directory rename all files with extension .raj
  in the subtree of directories rooted at root by changing the extension to .jar

  Keyword arguments:
  root        -- string: absolute path of a directory in the local file system
  followlinks -- boolean: follow links in path walk (default False)
  """

  if not os.path.isdir(root):
    logging.info("Argument root is not a directory")
    return

  if type(followlinks) not bool:
    followlinks = False
    logging.info("Invalid followlinks value, setting to False")

  for root, dirs, files in os.walk(root, followlinks=followlinks):
    for filename in files:
      if filename.endswith(".raj"):
        filename_new = filename[:-4] + ".jar"
        path_old = os.path.join(root, filename)
        path_new = os.path.join(root, filename_new)
        logging.info("Renaming {path_old} into {path_new}...")
        try:
          os.rename(path_old, path_new)
          logging.info("Renamed!")
        except Exception as ex:
          logging.info("Exception:")
          logging.info(ex)
