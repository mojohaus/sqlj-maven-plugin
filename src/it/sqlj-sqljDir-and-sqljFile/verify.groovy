// Verify that source files were generated
assert new File( basedir, 'target/generated-sources/sqlj/org/acme/TestUNICODE.java' ).exists()
assert new File( basedir, 'target/generated-sources/sqlj/org/acme/TestUNICODE2.java' ).exists()
assert new File( basedir, 'target/generated-sources/sqlj/dummy/TestUNICODE3.java' ).exists()

// Verify that .ser files were generated
assert new File( basedir, 'target/generated-resources/sqlj/org/acme/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/org/acme/TestUNICODE2_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/dummy/TestUNICODE3_SJProfile0.ser' ).exists()

// Verify that source files were compiled (i.e. generated-sources/sqlj/ folder was added as source folder)
assert new File( basedir, 'target/classes/org/acme/TestUNICODE.class' ).exists()
assert new File( basedir, 'target/classes/org/acme/TestUNICODE2.class' ).exists()
assert new File( basedir, 'target/classes/dummy/TestUNICODE3.class' ).exists()

// Verify that generated-resources/sqlj/ folder was added as resource folder
assert new File( basedir, 'target/classes/org/acme/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/classes/org/acme/TestUNICODE2_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/classes/dummy/TestUNICODE3_SJProfile0.ser' ).exists()
