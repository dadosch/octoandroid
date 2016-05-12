package com.nairbspace.octoandroid.account_manager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;
import com.nairbspace.octoandroid.app.SetupApplication;
import com.nairbspace.octoandroid.domain.interactor.DefaultSubscriber;
import com.nairbspace.octoandroid.domain.interactor.DeletePrinterByName;
import com.nairbspace.octoandroid.ui.add_printer.AddPrinterActivity;

import javax.inject.Inject;

public class Authenticator extends AbstractAccountAuthenticator {

    private Context mContext;
    @Inject DeletePrinterByName mPrinterDetailsByName;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        SetupApplication.get(context).getAppComponent().inject(this);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent i = AddPrinterActivity.newIntent(mContext);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, i);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account) throws NetworkErrorException {
        String name = account.name;
        mPrinterDetailsByName.execute(new GetPrinterDetailsSubscriber(), name);

        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
        return result;
    }

    @RxLogSubscriber
    private final class GetPrinterDetailsSubscriber extends DefaultSubscriber<Boolean> {

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            // TODO should log this somehow in case to try later
        }

        @Override
        public void onNext(Boolean printer) {

        }
    }
}
