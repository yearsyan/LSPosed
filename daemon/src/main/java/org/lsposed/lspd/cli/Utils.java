package org.lsposed.lspd.cli;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;

import com.beust.jcommander.Parameters;

import org.lsposed.lspd.ILSPManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Utils {

    @Parameters(commandNames = "help", hidden = true)
    public static class HelpCommandStub {}

    private static HashMap<String,PackageInfo> packagesMap;

    private static void initPackagesMap(ILSPManagerService managerService) throws RemoteException {
        var packages =
                managerService.getInstalledPackagesFromAllUsers(PackageManager.GET_META_DATA | PackageManager.MATCH_UNINSTALLED_PACKAGES, true).getList();
        packagesMap = new HashMap<>();
        for (var packageInfo: packages) {
            packagesMap.put(packageInfo.packageName, packageInfo);
        }
    }


    public static boolean validXposedModule(ILSPManagerService managerService, String packageName) throws RemoteException {
        if (packagesMap == null) {
            initPackagesMap(managerService);
        }

        return packagesMap.containsKey(packageName) &&
                Objects.requireNonNull(packagesMap.get(packageName)).applicationInfo.metaData.containsKey("xposedminversion");

    }

    public static boolean validPackageNameAndUserId(ILSPManagerService managerService, String packageName, int userId) throws RemoteException {
        if (packagesMap == null) {
            initPackagesMap(managerService);
        }

        return packagesMap.containsKey(packageName)
                && (Objects.requireNonNull(packagesMap.get(packageName)).applicationInfo.uid) / 100000 == userId;

    }

}
