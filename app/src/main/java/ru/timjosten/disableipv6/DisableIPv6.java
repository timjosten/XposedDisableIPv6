package ru.timjosten.disableipv6;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;

public class DisableIPv6 implements IXposedHookLoadPackage
{
  private static final String TAG = DisableIPv6.class.getSimpleName() + ": ";

  public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam)
  throws Throwable
  {
    if(!lpparam.packageName.equals("android"))
      return;

    try
    {
      XposedHelpers.findAndHookMethod("com.android.server.NetworkManagementService", lpparam.classLoader, "enableIpv6", String.class,
      new XC_MethodHook()
      {
        @Override
        protected void beforeHookedMethod(MethodHookParam param)
        throws Throwable
        {
          try
          {
            XposedHelpers.callMethod(param.thisObject, "disableIpv6", param.args[0]);
            param.setResult(null);
          }
          catch(Throwable t)
          {
            XposedBridge.log(TAG + t);
          }
        }
      });
      XposedHelpers.findAndHookMethod("android.net.ip.IpManager.ProvisioningConfiguration.Builder", lpparam.classLoader, "build",
      new XC_MethodHook()
      {
        @Override
        protected void beforeHookedMethod(MethodHookParam param)
        throws Throwable
        {
          try
          {
            XposedHelpers.callMethod(param.thisObject, "withoutIPv6");
          }
          catch(Throwable t)
          {
            XposedBridge.log(TAG + t);
          }
        }
      });
    }
    catch(Throwable t)
    {
      XposedBridge.log(TAG + t);
    }
  }
}
