package job;

import core.*;

@Job(name="word-counter")
public class WordCounter {
    /*
     * taskA: InPipeA1, InPipeA2
     * taskB: InPipeA1, InPipeA2, ...  OutPipeB1, OutPipeB2 ...
     * taskC: InPipeB1, InPipeB2, ...  OutPipeC1
     * taskD: InPipeD1
     * taskE: InPipeE1
     * taskF: InPipeC1, InPipeD1, InPipeE1, OutPipeF1
     * 
     * 
     * A(A1,A2) ---> B(B1,B2) ---> C(C1) ---> F(F1)
     *                             D(D1) ---> F(F1)
     *                             E(E1) ---> F(F1)
     * */
    
    @Task(name="text", out={"lines"})
    public void textSource(@Out OutPipe<String> linesPipe) {
        linesPipe.write("skdfjs\r\n");
    }
    
    @Task(in={"lines"}, out={"words"})
    public void splitWords(InPipe<String> linesPipe, @Out OutPipe<String> wordsPipe) {
        String line;
        while (true) {
            line = linesPipe.read();
            String words[] = line.split("\\s");
            for (String word : words) {
                wordsPipe.write(word);
            }
        }
    }
    
    @Task(in={"words"})
    public void count(InPipe<String> wordsPipe, Context ctx) {
        while (true) {
            String word = wordsPipe.read();
            if (ctx.get(word) == null) {
                ctx.set(word, new Integer(1));
            } else {
                ctx.set(word, new Integer((Integer)ctx.get(word) + 1));
            }
        }
    }
    
    @Task(parent="count", in={"words"}, out={"wordCounts"})
    public void countCmd(@In InPipe<String> wordsPipe, @Out OutPipe<WordCount> wordCountsPipe, Context ctx) {
        while (true) {
            for (String word : ctx.getMap().keySet()) {
                WordCount wc = new WordCount();
                wc.word = word;
                wc.count = (Integer)ctx.get(word);
                wordCountsPipe.write(wc);
            }
        }
    }
    
    @Task(in={"wordCounts"})
    public void merge(@In InPipe<WordCount> wordCounts, Context ctx) {
        while (true) {
            WordCount wc = wordCounts.read();
            if (ctx.get(wc.word) == null) {
                ctx.set(wc.word, wc.count);
            } else {
                ctx.set(wc.word, wc.count + (Integer)ctx.get(wc.word));
            }
        }
    }
    
    @Task(parent="merge", in={"wordCounts"})
    public void onWordCountsCmd(@In InPipe<WordCount> wordCounts, Context ctx) {
        while (true) {
            for (String word : ctx.getMap().keySet()) {
                System.out.println(word + ":" + ctx.get(word));
            }
        }
    }
    
    class WordCount {
        public String word;
        public int count;
    }
}