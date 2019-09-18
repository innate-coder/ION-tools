var csfFailureDetectTimer, htValue, cpCores, mtuSize, switchFabricNetMask, l3Connectivity, mntValue, oamManagementNetmask, primaryConfig, licenseFile, staticRoute, controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, mgLbManagementCidr, vf2vf;

var lbSlot, mgWorkingSlot, mgProtectSlot, cfp;

var freeCSFIps = [];
var mgType = ['mgWorking','mgProtect'];

// Global extension variables
csfFailureDetectTimer = $.stack_params.cbam.extensions.csfFailureDetectionTimer;
htValue = $.stack_params.cbam.extensions.hyperThreadingSupportValue;
mtuSize = $.stack_params.cbam.extensions.mtu;
mntValue = $.stack_params.cbam.extensions.multipleNicTasks;
mgLbManagementCidr = $.stack_params.cbam.extensions.mgLbManagementCidr;
controlSwitchFabricCidr = $.stack_params.cbam.extensions.controlSwitchFabricCidr;
dataSwitchFabric1Cidr = $.stack_params.cbam.extensions.dataSwitchFabric1Cidr;
dataSwitchFabric2Cidr = $.stack_params.cbam.extensions.dataSwitchFabric2Cidr;
dataSwitchFabric1Vlan = $.stack_params.cbam.extensions.dataSwitchFabric1Vlan;
dataSwitchFabric2Vlan = $.stack_params.cbam.extensions.dataSwitchFabric2Vlan;
vf2vf = $.stack_params.cbam.extensions.vf2vf;

var cCBAM = new classCBAM();
var cConfiguration = new classConfiguration();

// L3 connectivity variables
switchFabricNetMask = cCBAM.assignSwitchFabricNetMask(controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr);
l3Connectivity = $.stack_params.cbam.extensions.l3Connectivity;

// OAM node variables
oamManagementNetmask = $.stack_params.cbam.extensions.oamManagementNetmask;
primaryConfig = $.stack_params.cbam.extensions.primaryConfigFile;
licenseFile = $.stack_params.cbam.extensions.licenseFile;
staticRoute = $.stack_params.cbam.extensions.staticRoute;
oamActiveAddress = $.stack_params.cbam.resources.oamActiveManagementIp;
oamStandbyAddress = $.stack_params.cbam.resources.oamStandbyManagementIp;

csfDefaultGwIp = $.stack_params.cbam.resources.csfDefaultGwIp;
dsf1DefaultGwIp = $.stack_params.cbam.resources.dsf1DefaultGwIp;
dsf2DefaultGwIp = $.stack_params.cbam.resources.dsf2DefaultGwIp;

for(step in $.stack_params.cbam.resources.loadBalancerAspectGroup) {
  if(cCBAM.isNumeric(step)){

     lbSlot = $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.slot;

     $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios = cConfiguration.assignLbSmbios(step, lbSlot, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp);

  }
}

for(step in $.stack_params.cbam.resources.mgRedundantAspectGroup) {
   if(cCBAM.isNumeric(step)){

     mgWorkingSlot = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.slot;
     mgProtectSlot = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.slot;
     cpCores = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].cpCores;
     cfp = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].cfp;

     $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgWorkingSlot, cpCores, cfp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp);
     $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgProtectSlot, cpCores, cfp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp);

   }
}

$.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("A", csfDefaultGwIp);
$.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("B", csfDefaultGwIp);

cCBAM.assignBlacklistedGroups();
return $.stack_params

function classConfiguration() {
    //declare public members
    this.assignLbSmbios = assignLbSmbios;
    this.assignMgSmbios = assignMgSmbios;
    this.assignOamSmbios = assignOamSmbios;

    function assignLbSmbios(rgIndex, lbSlot, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp) {

      var lbSmbios = {};

      lbSmbios.main = "TiMOS: slot=" + lbSlot + " chassis=VSR card=iom-v mda/1=m20-v mda/2=isa-ms-v mda/3=isa-ms-v mda/4=isa-ms-v ofabric/1=2:" + dataSwitchFabric1Vlan + " ofabric/2=3:" + dataSwitchFabric2Vlan + " fswo=" + csfFailureDetectTimer + " ht=" + htValue + " mnt=" + mntValue + " mtu=" + mtuSize + " vf2vf=" + vf2vf;

      if ( l3Connectivity == "0" ) {

        lbSmbios.csf = "";
        lbSmbios.dsf1 = "";
        lbSmbios.dsf2 = "";
      }
      else if ( l3Connectivity == "1" ) {

        lbSmbios.csf = ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48";
        lbSmbios.dsf1 = ";" + switchFabricNetMask[1] + ";" + dsf1DefaultGwIp + ";18;48" + " l3fab/2=";
        lbSmbios.dsf2 = ";" + switchFabricNetMask[2] + ";" + dsf2DefaultGwIp + ";18;48";
      }
      else {

        throw new Error("Invalid extension value was provided");
      }

      return lbSmbios;
    }

    function assignMgSmbios(rgIndex, mgSlot, cpCores, cfp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp) {

      var mgSmbios = {};

      mgSmbios.main = "TiMOS: slot=" + mgSlot + " chassis=VSR card=iom-v-mg mda/1=isa-mg-v mda/2=isa-ms-v mda/3=isa-ms-v mda/4=isa-ms-v ofabric/1=2:" + dataSwitchFabric1Vlan + " ofabric/2=3:" + dataSwitchFabric2Vlan + " fswo=" + csfFailureDetectTimer + " ht=" + htValue + " cpcores=" + cpCores + " cfp=" + cfp + " mtu=" + mtuSize + " vf2vf=" + vf2vf;

      if ( l3Connectivity == "0" ) {

        mgSmbios.csf = "";
        mgSmbios.dsf1 = "";
        mgSmbios.dsf2 = "";
      }
      else if ( l3Connectivity == "1" ) {

        mgSmbios.csf = ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48";
        mgSmbios.dsf1 = ";" + switchFabricNetMask[1] + ";" + dsf1DefaultGwIp + ";18;48" + " l3fab/2=";
        mgSmbios.dsf2 = ";" + switchFabricNetMask[2] + ";" + dsf2DefaultGwIp + ";18;48";
      }
      else {

        throw new Error("Invalid extension value was provided");
      }

      return mgSmbios;
    }

    function assignOamSmbios(oamSlot, csfDefaultGwIp) {

      var oamSmbios = {};

      oamSmbios.main = "TiMOS: slot=" + oamSlot + " chassis=VSR card=cpm-v address=" + oamActiveAddress + "/" + oamManagementNetmask + "@active address=" + oamStandbyAddress + "/" + oamManagementNetmask + "@standby " + staticRoute + " primary-config=" + primaryConfig + " license-file=" + licenseFile + " fswo=" + csfFailureDetectTimer + " mtu=" + mtuSize;

      if ( l3Connectivity == "0" ) {

        oamSmbios.csf = "";
      }
      else if ( l3Connectivity == "1" ) {

        oamSmbios.csf = ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48";
      }
      else {

        throw new Error("Invalid extension value was provided");
      }

      return oamSmbios;
    }

}

function classCBAM() {
    //declare public members
    this.operationIsInstantiation = operationIsInstantiation;
    this.operationIsScaling = operationIsScaling;
    this.operationIsHealing = operationIsHealing;
    this.operationIsUpgrade = operationIsUpgrade;
    this.operationIsAASignatureUpdate = operationIsAASignatureUpdate;
    this.isNumeric = isNumeric;
    this.assignBlacklistedGroups = assignBlacklistedGroups;
    this.assignSwitchFabricNetMask = assignSwitchFabricNetMask;

    function init() {
        getSubnetInfo();
    }

    init();

    function isNumeric(n) {
       return !isNaN(parseFloat(n)) && isFinite(n)
    }

    function operationIsInstantiation() {

        if ($.operation_params.flavourId != undefined) {
            if ($.resource_model.resources == undefined) {
                return true;
            }
        }

        return false;
    }

    function operationIsScaling() {

        if ($.operation_params.type != undefined) {
            if (($.operation_params.type.toLowerCase() == "out") || ($.operation_params.type.toLowerCase() == "in")) {
                return true;
            }
        }

        return false;
    }

    function operationIsHealing() {

        if ($.operation_params.additionalParams != undefined) {
            if ($.operation_params.additionalParams.vnfcToHeal != undefined) {
                return true;
            }
        }

        return false;
    }

    function operationIsAASignatureUpdate() {

        if ($.operation_params.additionalParams != undefined) {
            if ($.operation_params.additionalParams.vmReInstantiation != undefined) {
                return true;
            }
        }

        return false;
    }

    function operationIsUpgrade() {

        if (!operationIsInstantiation() && !operationIsScaling() && !operationIsHealing() && !operationIsAASignatureUpdate()) {
            return true;
        }
        return false;
    }

    function assignBlacklistedGroups() {

      // assign blacklisted group for overcoming heat limitation when scale-in "0" index
      lbBlacklistedIds = $.stack_params.cbam.resources.loadBalancerAspectGroup.blacklist;
      mgBlacklistedIds = $.stack_params.cbam.resources.mgRedundantAspectGroup.blacklist;

      if (lbBlacklistedIds.indexOf("0") >= 0) {
          $.stack_params.cbam.resources.loadBalancerAspectGroup["0"] = {};
          $.stack_params.cbam.resources.loadBalancerAspectGroup["0"].present = 0;
      }
      if (mgBlacklistedIds.indexOf("0") >= 0) {
          $.stack_params.cbam.resources.mgRedundantAspectGroup["0"] = {};

          for (var iter = 0; iter < 2; iter++)
          {
            var item = mgType[iter];
            $.stack_params.cbam.resources.mgRedundantAspectGroup["0"][item] = {};
            $.stack_params.cbam.resources.mgRedundantAspectGroup["0"][item].present = 0;
          }
      }

      return 0;
    }

    function assignSwitchFabricNetMask (controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr) {

       var controlSwitchNetMask = "";
       var dataSwitchNet1Mask = "";
       var dataSwitchNet2Mask = "";

       controlSwitchNetMask = controlSwitchFabricCidr.split('/')[1];
       dataSwitchNet1Mask = dataSwitchFabric1Cidr.split('/')[1];
       dataSwitchNet2Mask = dataSwitchFabric2Cidr.split('/')[1];

       return [controlSwitchNetMask.toString(), dataSwitchNet1Mask.toString(), dataSwitchNet2Mask.toString()];
    }

    function getSubnetInfo () {

      if (dataSwitchFabric1Cidr.indexOf(":") !=-1) {
        $.stack_params.cbam.resources.dsf1DefaultGwIp = "0000:0000:0000:0000:0000:0000:0000:0000";
      } else if (dataSwitchFabric1Cidr.indexOf(".") !=-1){
        $.stack_params.cbam.resources.dsf1DefaultGwIp = "0.0.0.0";
      } else {
        throw new Error("Invalid DSF1 CIDR address was provided");
      }

      if (dataSwitchFabric2Cidr.indexOf(":") !=-1) {
        $.stack_params.cbam.resources.dsf2DefaultGwIp = "0000:0000:0000:0000:0000:0000:0000:0000";
      } else if (dataSwitchFabric2Cidr.indexOf(".") !=-1){
        $.stack_params.cbam.resources.dsf2DefaultGwIp = "0.0.0.0";
      } else {
        throw new Error("Invalid DSF2 CIDR address was provided");
      }

      if (controlSwitchFabricCidr.indexOf(":") !=-1) {
        $.stack_params.cbam.resources.csfIpVersion = 6;
        $.stack_params.cbam.resources.csfDefaultGwIp = "0000:0000:0000:0000:0000:0000:0000:0000";
      }
      else if (controlSwitchFabricCidr.indexOf(".") !=-1){
        $.stack_params.cbam.resources.csfIpVersion = 4;
        $.stack_params.cbam.resources.csfDefaultGwIp = "0.0.0.0";
      } else {
        throw new Error("Invalid CSF CIDR address was provided");
      }

      if (mgLbManagementCidr.indexOf(":") !=-1) {
        $.stack_params.cbam.resources.mgLbManagementIpVersion = 6;
      } else if (mgLbManagementCidr.indexOf(".") !=-1){
        $.stack_params.cbam.resources.mgLbManagementIpVersion = 4;
      } else {
        throw new Error("Invalid MG/LB Management CIDR address was provided");
      }
    }
}
