package com.nairbspace.octoandroid.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nairbspace.octoandroid.net.OctoApi;
import com.nairbspace.octoandroid.net.OctoInterceptor;
import com.nairbspace.octoandroid.net.OctoPrintApiImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private static final String DUMMY_URL = "http://localhost"; // Will be changed at runtime

    @Singleton
    @Provides
    OctoInterceptor provideInterceptor() {
        return OctoInterceptor.get();
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(OctoInterceptor interceptor, HttpLoggingInterceptor loggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor);
//                .cache(cache) // TODO: implement cache
        return builder.build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES); // TODO: Change field naming policies
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(GsonConverterFactory converterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .client(okHttpClient)
                .baseUrl(DUMMY_URL);
    }

    @Provides
    @Singleton
    OctoApi provideOctoprintApi(Retrofit.Builder builder) {
        return builder.build().create(OctoApi.class);
    }

    @Provides
    @Singleton
    OctoPrintApiImpl provideApiImpl(OctoApi api) {
        return OctoPrintApiImpl.get(api);
    }
}