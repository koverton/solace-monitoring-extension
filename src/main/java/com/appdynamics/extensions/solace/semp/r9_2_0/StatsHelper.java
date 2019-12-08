package com.appdynamics.extensions.solace.semp.r9_2_0;

import com.appdynamics.extensions.solace.Helper;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.solacesystems.semp_jaxb.r9_2_0.reply.*;

import java.util.Map;

public class StatsHelper {


    static String getRedundantMsgSpoolStatus(RedundancyInfoType detail) {
        String result;
        try {
            result = detail
                    .getStatus()
                    .getDetail()
                    .getMessageSpoolStatus()
                    .getInternal()
                    .getRedundancy();
        }
        catch(Throwable t) {
            result = "AD-Disabled";
        }
        return result;
    }
    static String getRedundantActivityStatus(RedundancyInfoType detail) {
        String result;
        try {
            result = detail
                    .getStatus()
                    .getDetail()
                    .getActivityStatus()
                    .getSummary();
        }
        catch(Throwable t) {
            result = "AD-Disabled";
        }
        return result;
    }

    static String getRedundantNodeSpoolStatus(RedundancyDetailInfoType detail) {
        String result = "NOT-FOUND";
        try {
            result = detail.getMessageSpoolStatus().getInternal().getRedundancy();
        }
        catch(Throwable t) {}
        return result;
    }

    static void directMessagingOnly(RpcReply.Rpc.Show.Redundancy redundancy, Map<String, Object> result) {
        String primaryMsgSpoolStatus = getRedundantMsgSpoolStatus(redundancy.getVirtualRouters().getPrimary());
        String backupMsgSpoolStatus = getRedundantMsgSpoolStatus(redundancy.getVirtualRouters().getBackup());
        // If message-spool is completely disabled, they're running direct-only, which can be Active/Active
        if( primaryMsgSpoolStatus.equals("AD-Disabled") && backupMsgSpoolStatus.equals("AD-Disabled")
        ) {
            if( getRedundantActivityStatus(redundancy.getVirtualRouters().getPrimary()).equals("Local Active")
                    || getRedundantActivityStatus(redundancy.getVirtualRouters().getBackup()).equals("Local Active") ) {
                result.put(Metrics.Redundancy.IsActive, 1);
            }
        }
    }

    static Long countIngressDiscards(MessageSpoolStatsType stats) {
        return Helper.longOrDefault(stats.getSpoolUsageExceeded(), 0) +
                Helper.longOrDefault(stats.getMaxMessageSizeExceeded(), 0) +
                Helper.longOrDefault(stats.getSpoolShutdownDiscard(), 0) +
                Helper.longOrDefault(stats.getUserProfileDenyGuaranteed(), 0) +
                Helper.longOrDefault(stats.getNoLocalDeliveryDiscard(), 0) +
                Helper.longOrDefault(stats.getDestinationGroupError(), 0) +
                Helper.longOrDefault(stats.getLowPriorityMsgCongestionDiscard(), 0);
    }

    static Long countEgressDiscards(MessageSpoolStatsType stats) {
        return Helper.longOrDefault(stats.getTotalDeletedMessages(), 0) +
                Helper.longOrDefault(stats.getTotalTtlExpiredDiscardMessages(), 0) +
                Helper.longOrDefault(stats.getTotalTtlExpiredToDmqMessages(), 0) +
                Helper.longOrDefault(stats.getTotalTtlExpiredToDmqFailures(), 0) +
                Helper.longOrDefault(stats.getMaxRedeliveryExceededDiscardMessages(), 0) +
                Helper.longOrDefault(stats.getMaxRedeliveryExceededToDmqMessages(), 0) +
                Helper.longOrDefault(stats.getMaxRedeliveryExceededToDmqFailures(), 0) +
                Helper.longOrDefault(stats.getTotalTtlExceededDiscardMessages(), 0);
    }

    static int combineConfigStatus(QendptInfoType info) {
        int a = info.getIngressConfigStatus().equals("Up") ? 1 : 0;
        int b = info.getEgressConfigStatus().equals("Up") ? 1 : 0;
        if (a >= 1 && b >= 1) return 1;
        return 0;
    }
}
