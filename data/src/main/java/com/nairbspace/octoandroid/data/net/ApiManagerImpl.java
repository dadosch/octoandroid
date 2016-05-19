package com.nairbspace.octoandroid.data.net;

import com.nairbspace.octoandroid.data.db.PrinterDbEntity;
import com.nairbspace.octoandroid.data.entity.ConnectEntity;
import com.nairbspace.octoandroid.data.entity.ConnectionEntity;
import com.nairbspace.octoandroid.data.entity.FileCommandEntity;
import com.nairbspace.octoandroid.data.entity.FilesEntity;
import com.nairbspace.octoandroid.data.entity.PrinterStateEntity;
import com.nairbspace.octoandroid.data.entity.VersionEntity;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.http.Body;
import retrofit2.http.Url;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class ApiManagerImpl implements ApiManager {
    private final OctoApi mOctoApi;

    @Inject
    public ApiManagerImpl(OctoApi octoApi) {
        mOctoApi = octoApi;
    }

    @Override
    public Observable<VersionEntity> getVersion() {
        return mOctoApi.getVersion();
    }

    @Override
    public Observable<ConnectionEntity> getConnection() {
        return mOctoApi.getConnection();
    }

    @Override
    public Observable<Object> postConnect(@Body ConnectEntity connectEntity) {
        return mOctoApi.postConnect(connectEntity);
    }

    @Override
    public Observable<PrinterStateEntity> getPrinter() {
        return mOctoApi.getPrinter();
    }

    @Override
    public Observable<FilesEntity> getAllFiles() {
        return mOctoApi.getAllFiles();
    }

    @Override
    public Observable<Object> sendJobCommand(@Body HashMap<String, String> command) {
        return mOctoApi.sendJobCommand(command);
    }

    @Override
    public Observable<Object> startFilePrint(@Url String url, @Body FileCommandEntity fileCommandEntity) {
        return mOctoApi.startFilePrint(url, fileCommandEntity);
    }

    @Override
    public Func1<PrinterDbEntity, Observable<VersionEntity>> funcGetVersion() {
        return new Func1<PrinterDbEntity, Observable<VersionEntity>>() {
            @Override
            public Observable<VersionEntity> call(PrinterDbEntity printerDbEntity) {
                return getVersion();
            }
        };
    }

    @Override
    public Func1<ConnectionEntity, Observable<ConnectionEntity>> funcGetConnection() {
        return new Func1<ConnectionEntity, Observable<ConnectionEntity>>() {
            @Override
            public Observable<ConnectionEntity> call(ConnectionEntity connectionEntity) {
                return getConnection();
            }
        };
    }

    @Override
    public Func1<ConnectEntity, Observable<?>> connectToPrinter() {
        return new Func1<ConnectEntity, Observable<?>>() {
            @Override
            public Observable<?> call(ConnectEntity connectEntity) {
                return postConnect(connectEntity);
            }
        };
    }

    @Override
    public Func1<FileCommandEntity, Observable<?>> funcStartFilePrint(final String apiUrl) {
        return new Func1<FileCommandEntity, Observable<?>>() {
            @Override
            public Observable<?> call(FileCommandEntity fileCommandEntity) {return startFilePrint(apiUrl, fileCommandEntity);
            }
        };
    }
}
