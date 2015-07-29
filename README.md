<<<<<<< HEAD
# stream
public class Runner {
    private OutPipe<java.lang.String> lines = new TcpOutPipe<java.lang.String>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void textSource() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.textSource(lines);
            }
        });
        cmdThread.start();
    }
}
public class Runner {
    private InPipe<java.lang.String> lines = new TcpInPipe<java.lang.String>();
    private OutPipe<java.lang.String> words = new TcpOutPipe<java.lang.String>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void splitWords() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.splitWords(lines, words);
            }
        });
        cmdThread.start();
    }
}
public class Runner {
    private InPipe<java.lang.String> words = new TcpInPipe<java.lang.String>();
    private OutPipe<job.WordCounter.WordCount> wordCounts = new TcpOutPipe<job.WordCounter.WordCount>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void countCmd() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.countCmd(words, wordCounts, ctx);
            }
        });
        cmdThread.start();
    }
}
public class Runner {
    private InPipe<job.WordCounter.WordCount> wordCounts = new TcpInPipe<job.WordCounter.WordCount>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void onWordCountsCmd() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.onWordCountsCmd(wordCounts, ctx);
            }
        });
        cmdThread.start();
    }
}
public class Runner {
    private InPipe<java.lang.String> words = new TcpInPipe<java.lang.String>();
    private OutPipe<job.WordCounter.WordCount> wordCounts = new TcpOutPipe<job.WordCounter.WordCount>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void countCmd() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.countCmd(words, wordCounts, ctx);
            }
        });
        cmdThread.start();
    }
    public void count() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.count(words, ctx);
            }
        });
        cmdThread.start();
    }
}
public class Runner {
    private InPipe<job.WordCounter.WordCount> wordCounts = new TcpInPipe<job.WordCounter.WordCount>();
    private Context ctx = new Context;
    private job.WordCounter job = new job.WordCounter();
    public void onWordCountsCmd() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.onWordCountsCmd(wordCounts, ctx);
            }
        });
        cmdThread.start();
    }
    public void merge() {
        Thread cmdThread = new Thread(new Runnable(){
            @Override
            public void run() {
                 job.merge(wordCounts, ctx);
            }
        });
        cmdThread.start();
    }
}
=======
# netpipe

a distributed computing framework based on netpipe
>>>>>>> 26782bbb0c6371f32d3510f3f40ec35e56dae92f
