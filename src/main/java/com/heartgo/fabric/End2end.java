/*
 *  Copyright 2016, 2017 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.heartgo.fabric;

import com.heartgo.myutil.Config;
import com.heartgo.utils.SampleOrg;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.InvalidProtocolBufferRuntimeException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;






public class End2end {

    private static final Config testConfig = Config.getConfig();

    HFClient client;

    private ChaincodeID chaincodeID= ChaincodeID.newBuilder().setName(testConfig.CHAIN_CODE_NAME)
            .setVersion(testConfig.CHAIN_CODE_VERSION)
            .setPath(testConfig.CHAIN_CODE_PATH).build();
    private Collection<SampleOrg> testSampleOrgs;



    public  ClientBean InitClient() {
        try {
            testSampleOrgs = testConfig.getIntegrationTestsSampleOrgs();
            //Set up hfca for each sample org

            for (SampleOrg sampleOrg : testSampleOrgs) {
                String caURL = sampleOrg.getCALocation();

                sampleOrg.setCAClient(HFCAClient.createNewInstance(caURL, null));
            }
            Collection<SampleOrg>  sampleorgs = new CheckConfig().checkConfig(testConfig);
            ////////////////////////////
            // Setup client
            //Create instance of client.
            SetUp setup = new SetUp(testConfig, testConfig.TEST_ADMIN_NAME, testConfig.TESTUSER_1_NAME);
            client = setup.SetupClient();
            setup.InitUsers(sampleorgs);


            //Todo change more beautiful; thanks to zhangpeng
            RunChannel runChannel = new RunChannel();

            SampleOrg sampleOrg = testConfig.getIntegrationTestsSampleOrg("peerOrg2");

            ClientBean newClienBean = new ClientBean(client, chaincodeID, sampleOrg, runChannel);

            Channel newChannel = constructChannel(testConfig.BAR_CHANNEL_NAME, client, sampleOrg);
            newClienBean.setChannel(newChannel);

            return newClienBean;


        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }
    public ClientBean composeClient() {
        try {
            testSampleOrgs = testConfig.getIntegrationTestsSampleOrgs();
            //Set up hfca for each sample org

            for (SampleOrg sampleOrg : testSampleOrgs) {
                String caURL = sampleOrg.getCALocation();

                sampleOrg.setCAClient(HFCAClient.createNewInstance(caURL, null));
            }
            Collection<SampleOrg>  sampleorgs = new CheckConfig().checkConfig(testConfig);
            ////////////////////////////
            // Setup client
            //Create instance of client.
            SetUp setup = new SetUp(testConfig, testConfig.TEST_ADMIN_NAME, testConfig.TESTUSER_1_NAME);
            client = setup.SetupClient();
            setup.SetupUsers(sampleorgs);



            //Todo change more beautiful; thanks to zhangpeng
            RunChannel runChannel = new RunChannel();

            SampleOrg sampleOrg = testConfig.getIntegrationTestsSampleOrg("peerOrg2");

            out("Compose Client sampleOrg is " + sampleOrg.getUser(testConfig.TESTUSER_1_NAME));
            ClientBean newClienBean = new ClientBean(client, chaincodeID, sampleOrg, runChannel);

            Channel newChannel = reconstructChannel(testConfig.BAR_CHANNEL_NAME, client, sampleOrg);

            newClienBean.setChannel(newChannel);
            return newClienBean;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }

    public void InstallChainCode(ClientBean newClientBean){

       try {

           RunChannel runchannel  = newClientBean.getRunchannel();


           runchannel.Inatall(newClientBean.getClient(),newClientBean.getChannel(),
                                newClientBean.getSampleorg(),newClientBean.getChaincodeid());

           newClientBean.setRunchannel(runchannel);
    } catch (Exception e) {
        e.printStackTrace();
       }
    }

    public void  InstantById(ClientBean newClientBean){

        RunChannel runchannel  = newClientBean.getRunchannel();
        runchannel.Instantiate(newClientBean.getClient(),newClientBean.getChaincodeid(),newClientBean.getChannel());
        newClientBean.setRunchannel(runchannel);


    }


    public void Transaction(ClientBean newClientBean, String[] transactionInfo) {
        try {

            RunChannel runChannel = newClientBean.getRunchannel();

            runChannel.SendtTansactionToPeers(newClientBean.getClient(),newClientBean.getChannel(),
                    newClientBean.getChaincodeid(),newClientBean.getSampleorg(), transactionInfo);

            out("\nTraverse the blocks for chain  "+ newClientBean.getChannel().getName());

            out("That's all folks!");



        } catch(Exception e) {
            e.printStackTrace();

        }

    }
    public void Transactionby(ClientBean newClientBean, String[] transactioninfo) {

        Channel channel = newClientBean.getChannel();
        HFClient client  = newClientBean.getClient();
        SampleOrg sampleOrg = newClientBean.getSampleorg();


        final String channelName = channel.getName();

        try {

//            final boolean changeContext = false; // BAR_CHANNEL_NAME.equals(channel.getName()) ? true : false;
            final boolean changeContext = testConfig.BAR_CHANNEL_NAME.equals(channel.getName());

            out("Running Channel %s with a delta %d", channelName, 0);


            channel.setTransactionWaitTime(testConfig.getTransactionWaitTime());
            channel.setDeployWaitTime(testConfig.getDeployWaitTime());

            ////////////////////////////
            // Send Query Proposal to all peers see if it's what we expect from end of End2endIT
            //

            //Set user context on client but use explicit user contest on each call.
            if (changeContext) {
                System.out.println("Transactionby USERS Client sampleOrg is " + sampleOrg.getUser("user1"));

                client.setUserContext(sampleOrg.getUser(testConfig.TESTUSER_1_NAME));
            }

            Invoke(client, channel, chaincodeID, transactioninfo, changeContext ? sampleOrg.getPeerAdmin() : null);

        } catch (Exception e){
            e.printStackTrace();

        }
    }
    CompletableFuture<BlockEvent.TransactionEvent> Invoke(HFClient client, Channel channel, ChaincodeID chaincodeID, String[] str, User user) {

        try {
            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> failed = new LinkedList<>();

            ///////////////
            /// Send transaction proposal to all peers
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeID);
            transactionProposalRequest.setFcn("invoke");
            transactionProposalRequest.setArgs(str);
            transactionProposalRequest.setProposalWaitTime(testConfig.getProposalWaitTime());
            if (user != null) { // specific user use that
                transactionProposalRequest.setUserContext(user);
            }
            out("sending transaction proposal to all peers with arguments: m");

            Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
            for (ProposalResponse response : invokePropResp) {
                if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                    out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }

            // Check that all the proposals are consistent with each other. We should have only one set
            // where all the proposals above are consistent.
            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(invokePropResp);
            if (proposalConsistencySets.size() != 1) {
                out(format("Expected only one set of consistent move proposal responses but got %d", proposalConsistencySets.size()));
            }

            out("Received %d transaction proposal responses. Successful+verified: %d . Failed: %d",
                    invokePropResp.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                out(firstTransactionProposalResponse.getProposalResponse().toString());

            }
            out("Successfully received transaction proposal responses.");

            ////////////////////////////
            // Send transaction to orderer
            out("Sending chaincode transaction() to orderer.");
            if (user != null) {
                return channel.sendTransaction(successful, user);
            }
            return channel.sendTransaction(successful);
        } catch (Exception e) {

            throw new CompletionException(e);

        }

    }

    public void QueryTransation(ClientBean newClientBean, String[] args){

        RunChannel runchannel  = newClientBean.getRunchannel();
        runchannel.SendQueryToPeers(newClientBean.getClient(), newClientBean.getChannel(), newClientBean.getChaincodeid(), args);

    }


    private static boolean checkInstantiatedChaincode(Channel channel, Peer peer, String ccName, String ccPath, String ccVersion) throws InvalidArgumentException, ProposalException {
        out("Checking instantiated chaincode: %s, at version: %s, on peer: %s", ccName, ccVersion, peer.getName());
        List<Query.ChaincodeInfo> ccinfoList = channel.queryInstantiatedChaincodes(peer);

        boolean found = false;

        for (Query.ChaincodeInfo ccifo : ccinfoList) {
            found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath()) && ccVersion.equals(ccifo.getVersion());
            if (found) {
                break;
            }

        }

        return found;
    }

    private Channel reconstructChannel(String name, HFClient client, SampleOrg sampleOrg) throws Exception {

        out("重建通道！");
        client.setUserContext(sampleOrg.getPeerAdmin());
        Channel newChannel = client.newChannel(name);

        for (String orderName : sampleOrg.getOrdererNames()) {
            newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    testConfig.getOrdererProperties(orderName)));
        }

        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Peer peer = client.newPeer(peerName, peerLocation, testConfig.getPeerProperties(peerName));
            Set<String> channels = client.queryChannels(peer);
            if (!channels.contains(name)) {
                throw new AssertionError(format("Peer %s does not appear to belong to channel %s", peerName, name));
            }
            newChannel.addPeer(peer);
            sampleOrg.addPeer(peer);
        }

        for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
                    testConfig.getEventHubProperties(eventHubName));
            newChannel.addEventHub(eventHub);
        }
        newChannel.initialize();
        for (Peer peer : newChannel.getPeers()) {

            if (!checkInstalledChaincode(client, peer, testConfig.CHAIN_CODE_NAME, testConfig.CHAIN_CODE_PATH, testConfig.CHAIN_CODE_VERSION)) {
                System.out.println(format("Peer %s is missing chaincode name: %s, path:%s, version: %s",
                        peer.getName(), testConfig.CHAIN_CODE_NAME, testConfig.CHAIN_CODE_PATH,testConfig. CHAIN_CODE_PATH));
            }

            if (!checkInstantiatedChaincode(newChannel, peer, testConfig.CHAIN_CODE_NAME,testConfig. CHAIN_CODE_PATH, testConfig.CHAIN_CODE_VERSION)) {

                System.out.println(format("Peer %s is missing instantiated chaincode name: %s, path:%s, version: %s",
                        peer.getName(), testConfig.CHAIN_CODE_NAME, testConfig.CHAIN_CODE_PATH,testConfig. CHAIN_CODE_PATH));
            }

        }
        return newChannel;
    }

    private static boolean checkInstalledChaincode(HFClient client, Peer peer, String ccName, String ccPath, String ccVersion) throws InvalidArgumentException, ProposalException {

        out("Checking installed chaincode: %s, at version: %s, on peer: %s", ccName, ccVersion, peer.getName());
        List<Query.ChaincodeInfo> ccinfoList = client.queryInstalledChaincodes(peer);

        boolean found = false;

        for (Query.ChaincodeInfo ccifo : ccinfoList) {

            found = ccName.equals(ccifo.getName()) && ccPath.equals(ccifo.getPath()) && ccVersion.equals(ccifo.getVersion());
            if (found) {
                break;
            }

        }

        return found;
    }

    //CHECKSTYLE.ON: Method length is 320 lines (max allowed is 150).

    private static Channel constructChannel(String name, HFClient client, SampleOrg sampleOrg) throws Exception {
        ////////////////////////////
        //Construct the channel
        //

        out("Constructing channel %s", name);

        //Only peer Admin org
        client.setUserContext(sampleOrg.getPeerAdmin());

        Collection<Orderer> orderers = new LinkedList<>();

        for (String orderName : sampleOrg.getOrdererNames()) {

            Properties ordererProperties = testConfig.getOrdererProperties(orderName);

            //example of setting keepAlive to avoid timeouts on inactive http2 connections.
            // Under 5 minutes would require changes to server side to accept faster ping rates.
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

            orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    ordererProperties));
        }

        //Just pick the first orderer in the list to create the channel.

        Orderer anOrderer = orderers.iterator().next();
        orderers.remove(anOrderer);

        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(testConfig.TEST_FIXTURES_PATH + "/sdkintegration/e2e-2Orgs/channel/" + name + ".tx"));
        System.out.println("channel path"+new File(testConfig.TEST_FIXTURES_PATH + "/sdkintegration/e2e-2Orgs/channel/" + name + ".tx"));

        //Create channel that has only one signer that is this orgs peer admin. If channel creation policy needed more signature they would need to be added too.
        Channel newChannel = client.newChannel(name, anOrderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, sampleOrg.getPeerAdmin()));

        out("Created channel %s", name);

        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = testConfig.getPeerProperties(peerName); //test properties for peer.. if any.
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            //Example of setting specific options on grpc's NettyChannelBuilder
            peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            newChannel.joinPeer(peer);
            out("Peer %s joined channel %s", peerName, name);
            sampleOrg.addPeer(peer);
        }

        for (Orderer orderer : orderers) { //add remaining orderers if any.
            newChannel.addOrderer(orderer);
        }

        for (String eventHubName : sampleOrg.getEventHubNames()) {

            final Properties eventHubProperties = testConfig.getEventHubProperties(eventHubName);

            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
            eventHubProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

            EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
                    eventHubProperties);
            newChannel.addEventHub(eventHub);
        }

        newChannel.initialize();

        out("Finished initialization channel %s", name);

        return newChannel;

    }

    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }

    private static void waitOnFabric(int additional) {
        //NOOP today

    }

    private static final Map<String, String> TX_EXPECTED;

    static {
        TX_EXPECTED = new HashMap<>();
        TX_EXPECTED.put("readset1", "Missing readset for channel bar block 1");
        TX_EXPECTED.put("writeset1", "Missing writeset for channel bar block 1");
    }

    static void blockWalker(Channel channel) throws InvalidArgumentException, ProposalException, IOException {
        try {
            BlockchainInfo channelInfo = channel.queryBlockchainInfo();

            for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
                BlockInfo returnedBlock = channel.queryBlockByNumber(current);
                final long blockNumber = returnedBlock.getBlockNumber();

                out("current block number %d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash()));
                out("current block number %d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash()));
                out("current block number %d has calculated block hash is %s", blockNumber, Hex.encodeHexString(SDKUtils.calculateBlockHash(blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash())));

                final int envelopeCount = returnedBlock.getEnvelopeCount();
           //     assertEquals(1, envelopeCount);
                out("current block number %d has %d envelope count:", blockNumber, returnedBlock.getEnvelopeCount());
                int i = 0;
                for (BlockInfo.EnvelopeInfo envelopeInfo : returnedBlock.getEnvelopeInfos()) {
                    ++i;

                    out("  Transaction number %d has transaction id: %s", i, envelopeInfo.getTransactionID());
                    final String channelId = envelopeInfo.getChannelId();
               //     assertTrue("foo".equals(channelId) || "bar".equals(channelId));

                    out("  Transaction number %d has channel id: %s", i, channelId);
                    out("  Transaction number %d has epoch: %d", i, envelopeInfo.getEpoch());
                    out("  Transaction number %d has transaction timestamp: %tB %<te,  %<tY  %<tT %<Tp", i, envelopeInfo.getTimestamp());
                    out("  Transaction number %d has type id: %s", i, "" + envelopeInfo.getType());

                    if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                        BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

                        out("  Transaction number %d has %d actions", i, transactionEnvelopeInfo.getTransactionActionInfoCount());
                  //      assertEquals(1, transactionEnvelopeInfo.getTransactionActionInfoCount()); // for now there is only 1 action per transaction.
                        out("  Transaction number %d isValid %b", i, transactionEnvelopeInfo.isValid());
                    //    assertEquals(transactionEnvelopeInfo.isValid(), true);
                        out("  Transaction number %d validation code %d", i, transactionEnvelopeInfo.getValidationCode());
                     //   assertEquals(0, transactionEnvelopeInfo.getValidationCode());

                        int j = 0;
                        for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                            ++j;
                            out("   Transaction action %d has response status %d", j, transactionActionInfo.getResponseStatus());
                    //        assertEquals(200, transactionActionInfo.getResponseStatus());
                            out("   Transaction action %d has response message bytes as string: %s", j,
                                    printableString(new String(transactionActionInfo.getResponseMessageBytes(), "UTF-8")));
                            out("   Transaction action %d has %d endorsements", j, transactionActionInfo.getEndorsementsCount());
                        //    assertEquals(2, transactionActionInfo.getEndorsementsCount());

                            for (int n = 0; n < transactionActionInfo.getEndorsementsCount(); ++n) {
                                BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(n);
                                out("Endorser %d signature: %s", n, Hex.encodeHexString(endorserInfo.getSignature()));
                                out("Endorser %d endorser: %s", n, new String(endorserInfo.getEndorser(), "UTF-8"));
                            }
                            out("   Transaction action %d has %d chaincode input arguments", j, transactionActionInfo.getChaincodeInputArgsCount());
                            for (int z = 0; z < transactionActionInfo.getChaincodeInputArgsCount(); ++z) {
                                out("     Transaction action %d has chaincode input argument %d is: %s", j, z,
                                        printableString(new String(transactionActionInfo.getChaincodeInputArgs(z), "UTF-8")));
                            }

                            out("   Transaction action %d proposal response status: %d", j,
                                    transactionActionInfo.getProposalResponseStatus());
                            out("   Transaction action %d proposal response payload: %s", j,
                                    printableString(new String(transactionActionInfo.getProposalResponsePayload())));

                            // Check to see if we have our expected event.
                            if (blockNumber == 2) {
                                ChaincodeEvent chaincodeEvent = transactionActionInfo.getEvent();
//                                assertNotNull(chaincodeEvent);
//
//                                assertTrue(Arrays.equals(EXPECTED_EVENT_DATA, chaincodeEvent.getPayload()));
//                                assertEquals(testTxID, chaincodeEvent.getTxId());
//                                assertEquals(CHAIN_CODE_NAME, chaincodeEvent.getChaincodeId());
//                                assertEquals(EXPECTED_EVENT_NAME, chaincodeEvent.getEventName());

                            }

                            TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
                            if (null != rwsetInfo) {
                                out("   Transaction action %d has %d name space read write sets", j, rwsetInfo.getNsRwsetCount());

                                for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
                                    final String namespace = nsRwsetInfo.getNamespace();
                                    KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

                                    int rs = -1;
                                    for (KvRwset.KVRead readList : rws.getReadsList()) {
                                        rs++;

                                        out("     Namespace %s read set %d key %s  version [%d:%d]", namespace, rs, readList.getKey(),
                                                readList.getVersion().getBlockNum(), readList.getVersion().getTxNum());

                                        if ("bar".equals(channelId) && blockNumber == 2) {
                                            if ("example_cc_go".equals(namespace)) {
                                                if (rs == 0) {
//                                                    assertEquals("a", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
                                                } else if (rs == 1) {
//                                                    assertEquals("b", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
                                                } else {
                                              //      fail(format("unexpected readset %d", rs));
                                                }

                                                TX_EXPECTED.remove("readset1");
                                            }
                                        }
                                    }

                                    rs = -1;
                                    for (KvRwset.KVWrite writeList : rws.getWritesList()) {
                                        rs++;
                                        String valAsString = printableString(new String(writeList.getValue().toByteArray(), "UTF-8"));

                                        out("     Namespace %s write set %d key %s has value '%s' ", namespace, rs,
                                                writeList.getKey(),
                                                valAsString);

                                        if ("bar".equals(channelId) && blockNumber == 2) {
                                            if (rs == 0) {
//                                                assertEquals("a", writeList.getKey());
//                                                assertEquals("400", valAsString);
                                            } else if (rs == 1) {
//                                                assertEquals("b", writeList.getKey());
//                                                assertEquals("400", valAsString);
                                            } else {
                                           //     fail(format("unexpected writeset %d", rs));
                                            }

                                            TX_EXPECTED.remove("writeset1");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!TX_EXPECTED.isEmpty()) {
              //  fail(TX_EXPECTED.get(0));
            }
        } catch (InvalidProtocolBufferRuntimeException e) {
            throw e.getCause();
        }
    }

    static String printableString(final String string) {
        int maxLogStringLength = 64;
        if (string == null || string.length() == 0) {
            return string;
        }

        String ret = string.replaceAll("[^\\p{Print}]", "?");

        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

        return ret;

    }

}
