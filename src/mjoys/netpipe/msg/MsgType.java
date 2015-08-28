package mjoys.netpipe.msg;

public enum MsgType {
    TLV,
    Data,
    RunTask,
    CreateWorker,
    DestroyWorkder,
    SetMaxQps,
    SwitchOutPipe,
    ReportStatus,;
}