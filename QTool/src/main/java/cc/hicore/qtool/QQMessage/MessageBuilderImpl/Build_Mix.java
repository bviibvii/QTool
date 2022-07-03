package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;

@XPItem(name = "Build_Mix",itemType = XPItem.ITEM_Api)
public class Build_Mix {
    @ApiExecutor
    @VerController
    public Object build(Object session, ArrayList msgElems) throws Exception {
        Method m = null;
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory");
        for (Method ma : clz.getDeclaredMethods()){
            if (ma.getReturnType().equals(MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"))){
                m = ma;
                break;
            }
        }

        Object MixMessageRecord;
        if (QQSessionUtils.getSessionID(session) == 10014) {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getChannelID(session), QQEnvUtils.getCurrentUin(), 10014);
        } else {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getGroupUin(session), QQEnvUtils.getCurrentUin(), 1);
        }
        MField.SetField(MixMessageRecord, "msgElemList", msgElems);
        MixMessageRecord = MMethod.CallMethodNoParam(MixMessageRecord, "rebuildMixedMsg", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"));
        return MixMessageRecord;
    }
}