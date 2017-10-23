package com.heartgo.fabric;


import com.heartgo.myutil.Config;
import com.heartgo.utils.SampleOrg;
import com.heartgo.utils.SampleStore;
import com.heartgo.utils.SampleUser;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.util.Collection;

import static java.lang.String.format;


import java.nio.file.Paths;

import static java.lang.String.format;



public class SetUp {
    public String ADMIN_NAME;
    public String USER_NAME;
    private Config config;

    public SetUp(Config config, String ADMIN_NAME, String USER_NAME) {
        this.ADMIN_NAME = ADMIN_NAME;
        this.USER_NAME = USER_NAME;
        this.config = config;
    }

    public HFClient SetupClient() {


        HFClient client = HFClient.createNewInstance();
        try {

            System.out.println("client:" + client.toString());
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

            //  System.out.println("client CryptoSuite:" + client.getCryptoSuite());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }


    public void InitUsers(Collection<SampleOrg> sampleOrgs) {

        File sampleStoreFile = new File(System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        //  System.out.println("pathname :"+System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            System.out.println("sample exit: Yes");
            sampleStoreFile.delete();
        }

        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        //  sampleStoreFile.deleteOnExit();

        //SampleUser can be any implementation that implements org.User Interface

        ////////////////////////////
        // get users for all orgs
        System.out.println("testSampleOrgs.size :" + sampleOrgs.size());
        try {
            for (SampleOrg sampleOrg : sampleOrgs) {

                HFCAClient ca = sampleOrg.getCAClient();

                final String orgName = sampleOrg.getName();
                final String mspid = sampleOrg.getMSPID();
                System.out.println("msp: "+ mspid);

                ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                System.out.println("orgName:" + orgName);
                System.out.println("mspid:" + mspid);
                HFCAInfo info = ca.info(); //just check if we connect at all.
                System.out.println("info tostring:" + info.toString());
                //  assertNotNull(info);
                String infoName = info.getCAName();
                System.out.println("infoName:" + infoName);
//                if (infoName != null && !infoName.isEmpty()) {
//                    assertEquals(ca.getCAName(), infoName);
//                }

                System.out.println("TEST_ADMIN_NAME:" + ADMIN_NAME);

                SampleUser admin = sampleStore.getMember(ADMIN_NAME, orgName);
                if (!admin.isEnrolled()) {  //Preregistered admin only needs to be enrolled with Fabric caClient.
                    System.out.println("!admin.isEnrolled()");

                    admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                    admin.setMspId(mspid);
                }

                sampleOrg.setAdmin(admin); // The admin of this org --

                SampleUser user = sampleStore.getMember(USER_NAME, sampleOrg.getName());
                if (!user.isRegistered()) {  // users need to be registered AND enrolled
                    System.out.println("!user.isRegistered()");
                    RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");
                    String secret  = ca.register(rr, admin);
                    System.out.println("秘钥 is "+ sampleOrg.getName()+"user name: " + user.getName()+ "fuck secret  :"+ secret + "fsd"+ admin.getName());
                    user.setEnrollmentSecret(secret);
                }
                if (!user.isEnrolled()) {
                    System.out.println("!user.isEnrolled()");
                    user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
                    user.setMspId(mspid);
                }
                sampleOrg.addUser(user); //Remember user belongs to this Org

                final String sampleOrgName = sampleOrg.getName();
                final String sampleOrgDomainName = sampleOrg.getDomainName();
                //     System.out.println("sampleOrgName:"+sampleOrgName+"  sampleOrgDomainName :"+sampleOrgDomainName);

                // src/test/fixture/sdkintegration/e2e-2Orgs/channel/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore/
//                 System.out.println("keystore:"+Paths.get(config.getTestChannelPath(), "crypto-config\\peerOrganizations",
//                         sampleOrgDomainName, format("users\\Admin@%s\\msp\\keystore", sampleOrgDomainName)).toFile());
//                 System.out.println("keyStore2:"+Paths.get(config.getTestChannelPath(), "crypto-config\\peerOrganizations", sampleOrgDomainName,
//                         format("users\\Admin@%s\\msp\\signcerts\\Admin@%s-cert.pem", sampleOrgDomainName, sampleOrgDomainName)).toFile());
                String str1 = config.getTestChannelPath() + "/crypto-config/peerOrganizations/" + sampleOrgDomainName + format("/users/Admin@%s/msp/keystore", sampleOrgDomainName);
                String str2 = config.getTestChannelPath() + "/crypto-config/peerOrganizations/" + sampleOrgDomainName + format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", sampleOrgDomainName, sampleOrgDomainName);
                File file = new File(str1);
                File file2 = new File(str2);

                System.out.println("file :" + file + " " + file.exists());
                System.out.println("file2 :" + file2 + " " + file2.exists());
                System.out.println("file :" + str1);
                System.out.println("file2 :" + str2);
                SampleUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName, sampleOrg.getMSPID(),
                        Util.findFileSk(file),
                        file2);

                sampleOrg.setPeerAdmin(peerOrgAdmin); //A special user that can create channels, join peers and install chaincode
                System.out.println("peerOrgAdmin:" + peerOrgAdmin.mspId + "  " + peerOrgAdmin.getName() + "  " + peerOrgAdmin.toString());
                System.out.println("Compose Client sampleOrg is " + sampleOrg.getUser("user1"));


            }


        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public void SetupUsers(Collection<SampleOrg> sampleOrgs) {

        File sampleStoreFile = new File(System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        //  System.out.println("pathname :"+System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            System.out.println("sample exit: Yes");
            sampleStoreFile.delete();
        }

        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        //  sampleStoreFile.deleteOnExit();

        //SampleUser can be any implementation that implements org.User Interface

        ////////////////////////////
        // get users for all orgs
        System.out.println("testSampleOrgs.size :" + sampleOrgs.size());
        try {
            boolean RESU = true;

            for (SampleOrg sampleOrg : sampleOrgs) {

                HFCAClient ca = sampleOrg.getCAClient();

                final String orgName = sampleOrg.getName();
                final String mspid = sampleOrg.getMSPID();
                ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                System.out.println("orgName:" + orgName);
                System.out.println("mspid:" + mspid);
                HFCAInfo info = ca.info(); //just check if we connect at all.
                System.out.println("info tostring:" + info.toString());
                //  assertNotNull(info);
                String infoName = info.getCAName();
                System.out.println("infoName:" + infoName);


                System.out.println("TEST_ADMIN_NAME:" + ADMIN_NAME);

                SampleUser admin = sampleStore.getMember(ADMIN_NAME, orgName);

                if (!admin.isEnrolled()) {  //Preregistered admin only needs to be enrolled with Fabric caClient.
                    System.out.println("!admin.isEnrolled()");

                    admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                    admin.setMspId(mspid);
                }

                sampleOrg.setAdmin(admin); // The admin of this org --

                SampleUser user = sampleStore.getMember(USER_NAME, sampleOrg.getName());

                if (!user.isEnrolled()) {
                    System.out.println("!user.isEnrolled()");
                    if ( RESU) {
                        RESU = false;

                        user.setEnrollment(ca.enroll(user.getName(), "cjGflpNCUsyD"));
                        user.setMspId(mspid);
                        System.out.println("fsd: "+ user.getName());
                    } else {
                        user.setEnrollment(ca.enroll(user.getName(), "mGrSrTAJoLYV"));

                        user.setMspId(mspid);
                        RESU = true;

                        System.out.println("fsd: "+ user.getName());

                    }
                }
                sampleOrg.addUser(user); //Remember user belongs to this Org

                final String sampleOrgName = sampleOrg.getName();
                final String sampleOrgDomainName = sampleOrg.getDomainName();

                SampleUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName, sampleOrg.getMSPID(),
                        findFileSk(Paths.get(config.getTestChannelPath(), "crypto-config/peerOrganizations/",
                                sampleOrgDomainName, format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile()),
                        Paths.get(config.getTestChannelPath(), "crypto-config/peerOrganizations/", sampleOrgDomainName,
                                format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", sampleOrgDomainName, sampleOrgDomainName)).toFile());

                sampleOrg.setPeerAdmin(peerOrgAdmin); //A special user that can create channels, join peers and install chaincode
                System.out.println("peerOrgAdmin:" + peerOrgAdmin.mspId + "  " + peerOrgAdmin.getName() + "  " + peerOrgAdmin.toString());
                System.out.println("SETUP USERS Client sampleOrg is " + sampleOrg.getUser("user1"));

            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
    File findFileSk(File directory) {

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];

    }
}