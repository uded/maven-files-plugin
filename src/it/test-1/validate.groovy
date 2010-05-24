def file=new File(basedir, 'target/config.xml')
log.debug("Checking for the file: " + file.toString())
assert file.exists()


