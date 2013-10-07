// Verify that correct source files were generated
assert new File( basedir, 'target/generated-sources/sqlj/org/acme/TestUNICODE.java' ).exists()
assert new File( basedir, 'target/generated-sources/sqlj/org/acme/TestUNICODE2.java' ).exists() == false
assert new File( basedir, 'target/generated-sources/sqlj/org/acme/TestUNICODE3.java' ).exists()

// Verify that correct .ser files were generated
assert new File( basedir, 'target/generated-resources/sqlj/org/acme/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/org/acme/TestUNICODE2_SJProfile0.ser' ).exists() == false
assert new File( basedir, 'target/generated-resources/sqlj/org/acme/TestUNICODE3_SJProfile0.ser' ).exists()

// Verify that source files were compiled (i.e. generated-sources/sqlj/ folder was added as source folder)
assert new File( basedir, 'target/classes/org/acme/TestUNICODE.class' ).exists()
assert new File( basedir, 'target/classes/org/acme/TestUNICODE3.class' ).exists()

// Verify that generated-resources/sqlj/ folder was added as resource folder
assert new File( basedir, 'target/classes/org/acme/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/classes/org/acme/TestUNICODE3_SJProfile0.ser' ).exists()
