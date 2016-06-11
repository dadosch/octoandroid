package com.nairbspace.octoandroid.mapper;

import android.support.annotation.NonNull;

import com.nairbspace.octoandroid.domain.executor.PostExecutionThread;
import com.nairbspace.octoandroid.domain.executor.ThreadExecutor;
import com.nairbspace.octoandroid.domain.model.CurrentHistory;
import com.nairbspace.octoandroid.domain.model.Websocket;
import com.nairbspace.octoandroid.exception.TransformErrorException;
import com.nairbspace.octoandroid.model.WebsocketModel;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

@SuppressWarnings("ConstantConditions")
public class WebsocketModelMapper extends MapperUseCase<Websocket, WebsocketModel> {

    private String mState = "";
    private String mFile = "";
    private String mApproxTotalPrintTime = "";
    private String mPrintTimeLeft = "";
    private String mPrintTime = "";
    private String mPrintedBytes = "";
    private String mPrintedFileSize = "";
    private int mCompletionProgress = 0;
    private boolean mOperational = false;
    private boolean mPaused = false;
    private boolean mPrinting = false;
    private boolean mPausedOrPrinting = false;
    private boolean mSdReady = false;
    private boolean mError = false;
    private boolean mReady = false;
    private boolean mClosedOrError = false;
    private boolean mFileLoaded = false;
    private String mTempTime = "";
    private float mActualTempBed = 0;
    private float mTargetTempBed = 0;
    private float mActualTempTool0 = 0;
    private float mTargetTempTool0 = 0;
    private float mActualTempTool1 = 0;
    private float mTargetTempTool1 = 0;

    @Inject
    public WebsocketModelMapper(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
    }

    @Override
    protected Observable<WebsocketModel> buildUseCaseObservable(final Websocket websocket) {
        return Observable.create(new Observable.OnSubscribe<WebsocketModel>() {
            @Override
            public void call(Subscriber<? super WebsocketModel> subscriber) {
                try {
                    if (websocket != null) {
                        parseWebsocket(websocket);
                        WebsocketModel websocketModel = mapToWebsocketModel();
                        subscriber.onNext(websocketModel);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(new TransformErrorException(e));
                }
            }
        });
    }

    private void parseWebsocket(@NonNull Websocket websocket) {
        if (websocket.current() != null) parseCurrentHistory(websocket.current());
    }

    private void parseCurrentHistory(@NonNull CurrentHistory currentHistory) {
        if (currentHistory.state() != null) parseState(currentHistory.state());
        if (currentHistory.job() != null) parseJob(currentHistory.job());
        if (currentHistory.progress() != null) parseProgress(currentHistory.progress());
        if (currentHistory.temps() != null) parseTemps(currentHistory.temps());
    }

    private void parseState(@NonNull CurrentHistory.State state) {
        if (state.text() != null) mState = state.text();
        if (state.flags() != null) parseFlags(state.flags());
    }

    private void parseJob(@NonNull CurrentHistory.Job job) {
        if (job.file() != null) parseFile(job.file());
        mApproxTotalPrintTime = DateTimeConverter.secondsToHHmmss(job.estimatedPrintTime());
    }

    private void parseFile(@NonNull CurrentHistory.Job.File file) {
        if (file.name() != null) mFile = file.name();
        mPrintedFileSize = ByteConverter.toReadableString(file.size());
        mFileLoaded = file.name() != null;
    }

    private void parseProgress(@NonNull CurrentHistory.Progress progress) {
        mPrintTime = DateTimeConverter.secondsToHHmmss(progress.printTime());
        mPrintTimeLeft = DateTimeConverter.secondsToHHmmss(progress.printTimeLeft());
        mPrintedBytes = ByteConverter.toReadableString(progress.filepos());
        mCompletionProgress = formatCompletion(progress.completion());
    }

    private void parseTemps(@NonNull List<CurrentHistory.Temps> temps) {
        if (!temps.isEmpty()) parseTemp(temps.get(0));
    }

    private void parseFlags(@NonNull CurrentHistory.State.Flags flags) {
        mOperational = flags.operational();
        mPaused = flags.paused();
        mPrinting = flags.printing();
        mPausedOrPrinting = flags.paused() || flags.printing();
        mSdReady = flags.sdReady();
        mError = flags.error();
        mReady = flags.ready();
        mClosedOrError = flags.closedOrError();
    }

    private void parseTemp(@NonNull CurrentHistory.Temps temp) {
        mTempTime = DateTimeConverter.unixSecondsToHHmmss(temp.time());
        if (temp.bed() != null) parseTempBed(temp.bed());
        if (temp.tool0() != null) parseTempTool0(temp.tool0());
        if (temp.tool1() != null) parseTempTool1(temp.tool1());
    }

    private void parseTempBed(@NonNull CurrentHistory.Temps.Bed bed) {
        mActualTempBed = (float) bed.actual();
        mTargetTempBed = (float) bed.target();
    }

    private void parseTempTool0(@NonNull CurrentHistory.Temps.Tool0 tool0) {
        mActualTempTool0 = (float) tool0.actual();
        mTargetTempTool0 = (float) tool0.target();
    }

    private void parseTempTool1(@NonNull CurrentHistory.Temps.Tool1 tool1) {
        mActualTempTool1 = (float) tool1.actual();
        mTargetTempTool1 = (float) tool1.target();
    }

    private WebsocketModel mapToWebsocketModel() {
        return WebsocketModel.builder()
                .state(mState)
                .file(mFile)
                .approxTotalPrintTime(mApproxTotalPrintTime)
                .printTime(mPrintTime)
                .printTimeLeft(mPrintTimeLeft)
                .printedBytes(mPrintedBytes)
                .printedFileSize(mPrintedFileSize)
                .completionProgress(mCompletionProgress)
                .operational(mOperational)
                .paused(mPaused)
                .printing(mPrinting)
                .pausedOrPrinting(mPausedOrPrinting)
                .sdReady(mSdReady)
                .error(mError)
                .ready(mReady)
                .closedOrError(mClosedOrError)
                .fileLoaded(mFileLoaded)

                .tempTime(mTempTime)
                .actualTempBed(mActualTempBed)
                .targetTempBed(mTargetTempBed)
                .actualTempTool0(mActualTempTool0)
                .targetTempTool0(mTargetTempTool0)
                .actualTempTool1(mActualTempTool1)
                .targetTempTool1(mTargetTempTool1)
                .build();
    }

    private int formatCompletion(Double completion) {
        int completionProgress = completion.intValue();
        return Math.abs(completionProgress);
    }
}