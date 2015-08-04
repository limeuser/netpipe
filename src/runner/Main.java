package runner;

import core.generator.JobGenerator;
import job.WordCounter;

public class Main {
    public static void main(String[] args) {
        JobGenerator.generate(WordCounter.class);
    }
}