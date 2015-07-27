package runner;

import job.WordCounter;

public class Main {
    public static void main(String[] args) {
        JobRunner.run(WordCounter.class);
    }
}