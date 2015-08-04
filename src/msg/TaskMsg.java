package msg;


public class TaskMsg {
    public class SwitchOutPipe {
        private String inPipeName;
        private String outPipeAddress;
        public String getInPipeName() {
            return inPipeName;
        }
        public void setInPipeName(String inPipeName) {
            this.inPipeName = inPipeName;
        }
        public String getOutPipeAddress() {
            return outPipeAddress;
        }
        public void setOutPipeAddress(String outPipeAddress) {
            this.outPipeAddress = outPipeAddress;
        }
    }
    
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
