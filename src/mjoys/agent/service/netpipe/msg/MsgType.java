package mjoys.agent.service.netpipe.msg;

public enum MsgType {
    CreateWorker,
    DestroyWorkder,
    SetMaxQps,
    SwitchOutPipe,
    GetTaskStatus,
    SetPipeAddress,
    StartWorker,
    StopWorker;
}