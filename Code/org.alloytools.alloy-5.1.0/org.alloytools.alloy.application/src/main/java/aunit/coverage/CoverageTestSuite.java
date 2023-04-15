package aunit.coverage;

import java.io.File;
import java.util.ArrayList;

import edu.mit.csail.sdg.parser.CompModule;

public class CoverageTestSuite {

    public ArrayList<TestCase> suite;
    public int                 num_vals;
    public int                 num_tests;
    public double              coverage;
    public int                 scope;
    public File                model;
    public String              orig;
    public CompModule          facts;
    public CompModule          nofacts;

    public CoverageTestSuite(File model, String orig, ArrayList<TestCase> suite, int scope, double coverage, int num_vals, int num_tests, CompModule facts, CompModule nofacts) {
        this.model = model;
        this.orig = orig;
        this.suite = new ArrayList<TestCase>();
        this.suite.addAll(suite);
        this.scope = scope;
        this.coverage = coverage;
        this.num_vals = num_vals;
        this.num_tests = num_tests;
        this.facts = facts;
        this.nofacts = nofacts;
    }
}
