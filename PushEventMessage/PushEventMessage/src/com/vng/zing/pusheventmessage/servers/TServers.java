/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.servers;

import com.vng.zing.pusheventmessage.handlers.PushEventMsgHandler;
import com.vng.zing.thriftserver.ThriftServers;
import com.vng.zing.pusheventmessage.thrift.PushEventMsgService;

/**
 *
 * @author namnq
 */
public class TServers {

    public boolean setupAndStart() {
        ThriftServers servers = new ThriftServers("pusheventmessage");

        PushEventMsgService.Processor processor = new PushEventMsgService.Processor(new PushEventMsgHandler());
        servers.setup(processor);
        return servers.start();
    }
}
