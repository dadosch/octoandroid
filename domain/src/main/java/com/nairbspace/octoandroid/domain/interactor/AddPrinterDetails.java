package com.nairbspace.octoandroid.domain.interactor;

import com.nairbspace.octoandroid.domain.pojo.AddPrinter;
import com.nairbspace.octoandroid.domain.exception.NullUseCaseBuilderException;
import com.nairbspace.octoandroid.domain.executor.PostExecutionThread;
import com.nairbspace.octoandroid.domain.executor.ThreadExecutor;
import com.nairbspace.octoandroid.domain.repository.PrinterRepository;

import javax.inject.Inject;

import rx.Observable;

public class AddPrinterDetails extends UseCase {

    private final PrinterRepository mPrinterRepository;
    private AddPrinter mAddPrinter;

    @Inject
    public AddPrinterDetails(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             PrinterRepository printerRepository) {
        super(threadExecutor, postExecutionThread);
        mPrinterRepository = printerRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mAddPrinter == null) {
            return Observable.error(new NullUseCaseBuilderException());
        }
        return mPrinterRepository.addPrinterDetails(mAddPrinter);
    }

    public void setAddPrinter(AddPrinter addPrinter) { // TODO need to see if can implement this other way
        mAddPrinter = addPrinter;
    }
}