// Verify that source files were generated
assert new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE.java' ).exists()
assert new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE2.java' ).exists()

// Verify that .ser files were generated
assert new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE2_SJProfile0.ser' ).exists()

// Verify that source files were compiled (i.e. generated-sources/sqlj/ folder was added as source folder)
assert new File( basedir, 'target/classes/test/com/tmme/x01/TestUNICODE.class' ).exists()
assert new File( basedir, 'target/classes/test/com/tmme/x01/TestUNICODE2.class' ).exists()

// Verify that generated-resources/sqlj/ folder was added as resource folder
assert new File( basedir, 'target/classes/test/com/tmme/x01/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/classes/test/com/tmme/x01/TestUNICODE2_SJProfile0.ser' ).exists()
