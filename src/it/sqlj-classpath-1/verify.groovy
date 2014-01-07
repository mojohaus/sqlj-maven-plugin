// Verify that source and .ser files were generated
assert new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE.java' ).exists()
assert new File( basedir, 'target/generated-sources/sqlj/test/com/tmme/x01/TestUNICODE2.java' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE_SJProfile0.ser' ).exists()
assert new File( basedir, 'target/generated-resources/sqlj/test/com/tmme/x01/TestUNICODE2_SJProfile0.ser' ).exists()
