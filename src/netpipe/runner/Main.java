package netpipe.runner;

import netpipe.generator.JobGenerator;
import netpipe.job.WordCounter;

public class Main {
    public static void main(String[] args) {
        JobGenerator.generate(WordCounter.class);
    }
}