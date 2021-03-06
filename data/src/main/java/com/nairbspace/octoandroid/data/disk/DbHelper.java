package com.nairbspace.octoandroid.data.disk;

import android.app.backup.BackupManager;

import com.nairbspace.octoandroid.data.db.PrinterDbEntity;
import com.nairbspace.octoandroid.data.db.PrinterDbEntityDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.Query;

/** Convenience methods for db related info */
@Singleton
public class DbHelper {

    private final PrinterDbEntityDao mPrinterDbEntityDao;
    private final PrefHelper mPrefHelper;
    private final BackupManager mBackupManager;
    private Map<String, Query<PrinterDbEntity>> mNameSearchQueryMap = new HashMap<>();
    private Map<Long, Query<PrinterDbEntity>> mIdSearchQueryMap = new HashMap<>();
    private Query<PrinterDbEntity> mListQuery;
    public final static Object sLock = new Object();

    @Inject
    public DbHelper(PrinterDbEntityDao printerDbEntityDao, PrefHelper prefHelper,
                    BackupManager backupManager) {
        mPrinterDbEntityDao = printerDbEntityDao;
        mPrefHelper = prefHelper;
        mBackupManager = backupManager;
    }

    public PrinterDbEntity getActivePrinterDbEntity() {
        long id = mPrefHelper.getActivePrinterId();
        return getPrinterFromDbById(id);
    }

    public PrinterDbEntity getPrinterFromDbByName(String name) {
        Query<PrinterDbEntity> query = mNameSearchQueryMap.get(name);
        if (query == null) {
            query = mPrinterDbEntityDao.queryBuilder()
                    .where(PrinterDbEntityDao.Properties.Name.eq(name))
                    .build();
            mNameSearchQueryMap.put(name, query);
        }

        try {
            synchronized (sLock) {
                // Need to call forCurrentThread or else it will throw DaoException
                return query.forCurrentThread().unique();
            }
        } catch (DaoException e) {
            return null; // Shouldn't happen
        }
    }

    public PrinterDbEntity getPrinterFromDbById(long printerId) {
        Query<PrinterDbEntity> query = mIdSearchQueryMap.get(printerId);
        if (query == null) {
            query = mPrinterDbEntityDao.queryBuilder()
                    .where(PrinterDbEntityDao.Properties.Id.eq(printerId))
                    .build();
            mIdSearchQueryMap.put(printerId, query);
        }
        try {
            synchronized (sLock) {
                // Need to call forCurrentThread or else it will throw DaoException
                return query.forCurrentThread().unique();
            }
        } catch (DaoException e) {
            return null; // Shouldn't happen
        }
    }

    public List<PrinterDbEntity> getPrintersFromDb() {
        if (mListQuery == null) {
            mListQuery = mPrinterDbEntityDao.queryBuilder().build();
        }

        synchronized (sLock) {
            // Need to call forCurrentThread or else it will throw DaoException
            return mListQuery.forCurrentThread().list();
        }
    }

    public void deletePrinterInDb(PrinterDbEntity printerDbEntity) {
        PrinterDbEntity oldPrinterDbEntity;
        if (printerDbEntity.getId() != null) {
            oldPrinterDbEntity = getPrinterFromDbById(printerDbEntity.getId());
        } else {
            oldPrinterDbEntity = getPrinterFromDbByName(printerDbEntity.getName());
        }
        if (oldPrinterDbEntity != null) {
            synchronized (sLock) {
                mPrinterDbEntityDao.delete(oldPrinterDbEntity);
                mBackupManager.dataChanged();
            }
        }
    }

    public long insertOrReplace(PrinterDbEntity printerDbEntity) {
        mPrefHelper.setSaveTimeMillis(System.currentTimeMillis());
        synchronized (sLock) {
            long id = mPrinterDbEntityDao.insertOrReplace(printerDbEntity);
            mBackupManager.dataChanged();
            return id;
        }
    }

    /**
     *
     * @param entity the entity to be checked
     * @return the result if entity name already exists as with a different id
     */
    public boolean doesPrinterNameExist(PrinterDbEntity entity) {
        List<PrinterDbEntity> list = getPrintersFromDb();
        if (list == null) return false;
        for (PrinterDbEntity printerDbEntity : list) {
            String dbName = printerDbEntity.getName();
            String editName = entity.getName();
            long dbId = printerDbEntity.getId();
            long editId = entity.getId();
            if (dbName.equals(editName) && dbId != editId) {
                return true;
            }
        }
        return false;
    }
}
