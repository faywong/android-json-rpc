android-json-rpc
================

android-json-rpc client library(fork from https://code.google.com/p/android-json-rpc)


How to use:
===========

Sample code:

        // Create client specifying JSON-RPC version 2.0
        JSONRPCClient client = JSONRPCClient.create(
                {your_server_service_url},
                JSONRPCParams.Versions.VERSION_2);
        client.setConnectionTimeout(3000);
        client.setSoTimeout(3000);
        // enable debug to inspect the raw request & response in your logcat output
        client.setDebug(true);

        try
        {
            int ret = client.callInt({remote_rpc_function}, params...);
        } catch (JSONRPCException e) {
            e.printStackTrace();
        }
