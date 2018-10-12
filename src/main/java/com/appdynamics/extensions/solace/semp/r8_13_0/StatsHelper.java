package com.appdynamics.extensions.solace.semp.r8_13_0;

import com.appdynamics.extensions.solace.Helper;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.solacesystems.semp_jaxb.r8_13_0.reply.MessageSpoolStatsType;
import com.solacesystems.semp_jaxb.r8_13_0.reply.QendptInfoType;
import com.solacesystems.semp_jaxb.r8_13_0.reply.RpcReply;

import java.util.Map;

public class StatsHelper {


    static void directMessagingOnly(RpcReply.Rpc.Show.Redundancy redundancy, Map<String, Object> result) {
        // If message-spool is completely disabled, they're running direct-only, which can be Active/Active
        if( redundancy.getVirtualRouters().getPrimary().getStatus().getDetail().getMessageSpoolStatus().equals("AD-Disabled")
                && redundancy.getVirtualRouters().getBackup().getStatus().getDetail().getMessageSpoolStatus().equals("AD-Disabled")
        ) {
            if( redundancy.getVirtualRouters().getPrimary().getStatus().getDetail().getActivityStatus().equals("Local Active")
                    || redundancy.getVirtualRouters().getBackup().getStatus().getDetail().getActivityStatus().equals("Local Active") ) {
                result.put(Metrics.Redundancy.IsActive, 1);
            }
        }
    }

    static Long countIngressDiscards(MessageSpoolStatsType stats) {
        return Helper.longOrDefault(stats.getSpoolUsageExceeded(), 0) +
                Helper.longOrDefault(stats.getSpoolUsageExceeded(), 0) +
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
