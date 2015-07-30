package runner;

public class TaskCmd {
    public class SetMaxQps {
        private String outPipeName;
        private String address;
        private int qps;
        
        public int getQps() {
            return qps;
        }
        public void setQps(int qps) {
            this.qps = qps;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
        public String getOutPipeName() {
            return outPipeName;
        }
        public void setOutPipeName(String outPipeName) {
            this.outPipeName = outPipeName;
        }
    }
}
