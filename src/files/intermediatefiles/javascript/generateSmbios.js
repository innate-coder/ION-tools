var csfFailureDetectTimer, htValue, cpCores, mtuSize, switchFabricNetMask, l3Connectivity, mntValue, oamManagementNetmask, primaryConfig, licenseFile, staticRoute, controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr, oamACsfIp, oamBCsfIp, lbControlSwitchFabricIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, mgLbManagementCidr, vf2vf;

var lbSlot, mgWorkingSlot, mgProtectSlot, cfp, mgWorkingControlSwitchFabricIp, mgProtectControlSwitchFabricIp;

var freeCSFIps = [];

var smbiosContents = ['main','dsf1','dsf2'];
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
var cControlSwitchFabric = new classControlSwitchFabric();
var cConfiguration = new classConfiguration();

// L3 connectivity variables
switchFabricNetMask = cControlSwitchFabric.assignSwitchFabricNetMask(controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr);
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

if ($.resource_model.resources != undefined && !(cCBAM.operationIsUpgrade())) {
  oamACsfIp = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMActive.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;
  oamBCsfIp = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMStandby.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;
}
else {
  oamACsfIp = freeCSFIps[0];
  oamBCsfIp = freeCSFIps[1];

  freeCSFIps.splice(0, 2);
}

for(step in $.stack_params.cbam.resources.loadBalancerAspectGroup) {
  if(cCBAM.isNumeric(step)){
     lbSlot = $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.slot;
     lbControlSwitchFabricIp = freeCSFIps.slice(0, 1);

     if (!(cCBAM.operationIsUpgrade())) {

       if (($.resource_model.resources != undefined) && (step in $.resource_model.resources.loadBalancerAspectGroup.resources)) {

          cConfiguration.assignLbSmbios(step, null, null, oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, true);
          $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.controlSwitchFabricIp = $.resource_model.resources.loadBalancerAspectGroup.resources[step].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_csfIp;
       }
       else {
         $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios = cConfiguration.assignLbSmbios(step, lbSlot, lbControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
         cControlSwitchFabric.assignLbSwitchFabricIps(step, lbControlSwitchFabricIp[0]);
       }
     }

     else {
       $.stack_params.cbam.resources.loadBalancerAspectGroup[step].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios = cConfiguration.assignLbSmbios(step, lbSlot, lbControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
       cControlSwitchFabric.assignLbSwitchFabricIps(step, lbControlSwitchFabricIp[0]);
     }
  }
}

for(step in $.stack_params.cbam.resources.mgRedundantAspectGroup) {
   if(cCBAM.isNumeric(step)){
     mgWorkingSlot = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.slot;
     mgProtectSlot = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.slot;
     cpCores = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].cpCores;
     cfp = $.stack_params.cbam.resources.mgRedundantAspectGroup[step].cfp;

     mgWorkingControlSwitchFabricIp = freeCSFIps.slice(0, 1);
     mgProtectControlSwitchFabricIp = freeCSFIps.slice(1, 2);

     if (!(cCBAM.operationIsUpgrade())) {

       if (($.resource_model.resources != undefined) && (step in $.resource_model.resources.mgRedundantAspectGroup.resources)) {

           cConfiguration.assignMgSmbios(step, null, cpCores, cfp, null, oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, true);

           $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.controlSwitchFabricIp = $.resource_model.resources.mgRedundantAspectGroup.resources[step].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_csfIp;
           $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.controlSwitchFabricIp = $.resource_model.resources.mgRedundantAspectGroup.resources[step].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_csfIp;
       }
       else {
         $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgWorkingSlot, cpCores, cfp, mgWorkingControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
         $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgProtectSlot, cpCores, cfp, mgProtectControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
         cControlSwitchFabric.assignMgSwitchFabricIps(step, mgWorkingControlSwitchFabricIp[0], mgProtectControlSwitchFabricIp[0]);
       }
     }

     else {
       $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgWorking.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgWorkingSlot, cpCores, cfp, mgWorkingControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
       $.stack_params.cbam.resources.mgRedundantAspectGroup[step].mgProtect.mgInstanceGroup["0"].mgServerInstance.smbios = cConfiguration.assignMgSmbios(step, mgProtectSlot, cpCores, cfp, mgProtectControlSwitchFabricIp[0], oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, false);
       cControlSwitchFabric.assignMgSwitchFabricIps(step, mgWorkingControlSwitchFabricIp[0], mgProtectControlSwitchFabricIp[0]);
     }
   }
}

if (!(cCBAM.operationIsUpgrade())) {

  if ($.resource_model.resources != undefined) {

    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.smbios = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMActive.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_smbios;
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.smbios = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMStandby.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_smbios;
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMActive.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMStandby.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;
  }

  else {
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("A", oamACsfIp, csfDefaultGwIp, csfDefaultGwIp, oamBCsfIp);
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("B", oamBCsfIp, csfDefaultGwIp, oamACsfIp, csfDefaultGwIp);
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = oamACsfIp;
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = oamBCsfIp;
    // Remove the first two IP from array since they were assigned to OAM-A,OAM-B
    freeCSFIps.splice(0, 2);
  }
}

else {

    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("A", oamACsfIp, csfDefaultGwIp, csfDefaultGwIp, oamBCsfIp);
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.smbios = cConfiguration.assignOamSmbios("B", oamBCsfIp, csfDefaultGwIp, oamACsfIp, csfDefaultGwIp);
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = oamACsfIp;
    $.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.controlSwitchFabricIp = oamBCsfIp;
}

cCBAM.assignBlacklistedGroups();
return $.stack_params

function classConfiguration() {
    //declare public members
    this.assignLbSmbios = assignLbSmbios;
    this.assignMgSmbios = assignMgSmbios;
    this.assignOamSmbios = assignOamSmbios;

    function assignLbSmbios(rgIndex, lbSlot, controlSwitchFabricIp, oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, deployedStatus) {

      if ( deployedStatus == true ) {

        $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios = {};

        var smbiosCount=0;
        for (var iter=0; iter<3; iter++) {

          var deployedLbSmbios = $.resource_model.resources.loadBalancerAspectGroup.resources[rgIndex].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_smbios;
          var regexSmbios = deployedLbSmbios.replace(/l3fab\/1=.*?;/, 'l3fab/1=;').replace(/l3fab\/2=.*?;/, 'l3fab/2=;');

          if (iter >= 1) {
              smbiosCount = iter + 1;
          }
          var splittedLbSmbios = regexSmbios.split('l3fab/')[smbiosCount];
          if (smbiosCount == 0) {
             $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios[smbiosContents[iter]] = regexSmbios.substr(0, regexSmbios.indexOf('l3fab/1')) + "l3fab/1=";
          }
          else if (smbiosCount == 2) {
             $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios[smbiosContents[iter]] = (splittedLbSmbios + "l3fab/" + smbiosCount + "=").slice(2);
          }
          else {
             $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.smbios[smbiosContents[iter]] = splittedLbSmbios.slice(2);
          }
        }

        return true;
      }

      else {

        var lbSmbios = {};

        lbSmbios.main = "TiMOS: slot=" + lbSlot + " chassis=VSR card=iom-v mda/1=m20-v mda/2=isa-ms-v mda/3=isa-ms-v mda/4=isa-ms-v ofabric/1=2:" + dataSwitchFabric1Vlan + " ofabric/2=3:" + dataSwitchFabric2Vlan + " fswo=" + csfFailureDetectTimer + " ht=" + htValue + " mnt=" + mntValue + " mtu=" + mtuSize + " vf2vf=" + vf2vf + " l3fab/0=" + controlSwitchFabricIp + ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48;" + oamACsfIp + ";" + oamBCsfIp + " l3fab/1=";
        lbSmbios.dsf1 = ";" + switchFabricNetMask[1] + ";" + dsf1DefaultGwIp + ";18;48" + " l3fab/2=";
        lbSmbios.dsf2 = ";" + switchFabricNetMask[2] + ";" + dsf2DefaultGwIp + ";18;48" + " newfab=" + l3Connectivity;

        return lbSmbios;
      }
    }

    function assignMgSmbios(rgIndex, mgSlot, cpCores, cfp, controlSwitchFabricIp, oamACsfIp, oamBCsfIp, csfDefaultGwIp, dsf1DefaultGwIp, dsf2DefaultGwIp, deployedStatus) {

      if ( deployedStatus == true ) {

        $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.smbios = {};
        $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.smbios = {};

        for (var nodeType=0; nodeType<2; nodeType++) {

           var smbiosCount=0;

           for (var iter=0; iter<3; iter++) {

             var deployedMgSmbios = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources[mgType[nodeType]].resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_smbios;
             var regexSmbios = deployedMgSmbios.replace(/l3fab\/1=.*?;/, 'l3fab/1=;').replace(/l3fab\/2=.*?;/, 'l3fab/2=;');

             if (iter >= 1) {
                 smbiosCount = iter + 1;
             }
             var splittedMgSmbios = regexSmbios.split('l3fab/')[smbiosCount];

             if (smbiosCount == 0) {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex][mgType[nodeType]].mgInstanceGroup["0"].mgServerInstance.smbios[smbiosContents[iter]] = regexSmbios.substr(0, regexSmbios.indexOf('l3fab/1')) + "l3fab/1=";
             }
             else if (smbiosCount == 2) {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex][mgType[nodeType]].mgInstanceGroup["0"].mgServerInstance.smbios[smbiosContents[iter]] = (splittedMgSmbios + "l3fab/" + smbiosCount + "=").slice(2);
             }
             else {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex][mgType[nodeType]].mgInstanceGroup["0"].mgServerInstance.smbios[smbiosContents[iter]] = splittedMgSmbios.slice(2);
             }
          }
        }

        return true;
      }

      else {

        var mgSmbios = {};

        mgSmbios.main = "TiMOS: slot=" + mgSlot + " chassis=VSR card=iom-v-mg mda/1=isa-mg-v mda/2=isa-ms-v mda/3=isa-ms-v mda/4=isa-ms-v ofabric/1=2:" + dataSwitchFabric1Vlan + " ofabric/2=3:" + dataSwitchFabric2Vlan + " fswo=" + csfFailureDetectTimer + " ht=" + htValue + " cpcores=" + cpCores + " cfp=" + cfp + " mtu=" + mtuSize + " vf2vf=" + vf2vf + " l3fab/0=" + controlSwitchFabricIp + ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48;" + oamACsfIp + ";" + oamBCsfIp + " l3fab/1=";
        mgSmbios.dsf1 = ";" + switchFabricNetMask[1] + ";" + dsf1DefaultGwIp + ";18;48" + " l3fab/2=";
        mgSmbios.dsf2 = ";" + switchFabricNetMask[2] + ";" + dsf2DefaultGwIp + ";18;48" + " newfab=" + l3Connectivity;

        return mgSmbios;
      }
    }

    function assignOamSmbios(oamSlot, controlSwitchFabricIp, csfDefaultGwIp, oamACsfIp, oamBCsfIp) {

      var oamSmbios = "";
      oamSmbios = "TiMOS: slot=" + oamSlot + " chassis=VSR card=cpm-v address=" + oamActiveAddress + "/" + oamManagementNetmask + "@active address=" + oamStandbyAddress + "/" + oamManagementNetmask + "@standby " + staticRoute + " primary-config=" + primaryConfig + " license-file=" + licenseFile + " fswo=" + csfFailureDetectTimer + " mtu=" + mtuSize + " l3fab/0=" + controlSwitchFabricIp + ";" + switchFabricNetMask[0] + ";" + csfDefaultGwIp + ";18;48;" + oamACsfIp + ";" + oamBCsfIp + " newfab=" + l3Connectivity;

      return oamSmbios;
    }

}

function classControlSwitchFabric() {
    //declare public members
    this.assignSwitchFabricNetMask = assignSwitchFabricNetMask;
    this.getIpv4RangeFromAddressAndNetmask = getIpv4RangeFromAddressAndNetmask;
    this.normalize = normalize;
    this.abbreviate = abbreviate;
    this._validate  = _validate;
    this._leftPad = _leftPad;
    this._hex2bin = _hex2bin;
    this._bin2hex = _bin2hex;
    this._addr2bin = _addr2bin;
    this._bin2addr = _bin2addr;
    this.getIpv6RangeFromCidr = getIpv6RangeFromCidr;
    this.convertToHex = convertToHex;
    this.convertZeroPadded = convertZeroPadded;
    this.assignMgSwitchFabricIps = assignMgSwitchFabricIps;
    this.assignLbSwitchFabricIps = assignLbSwitchFabricIps;

    function init() {
        fillArrayWithFreeIps();
    }

    init();

    function assignSwitchFabricNetMask (controlSwitchFabricCidr, dataSwitchFabric1Cidr, dataSwitchFabric2Cidr) {

       var controlSwitchNetMask = "";
       var dataSwitchNet1Mask = "";
       var dataSwitchNet2Mask = "";

       controlSwitchNetMask = controlSwitchFabricCidr.split('/')[1];
       dataSwitchNet1Mask = dataSwitchFabric1Cidr.split('/')[1];
       dataSwitchNet2Mask = dataSwitchFabric2Cidr.split('/')[1];

       return [controlSwitchNetMask.toString(), dataSwitchNet1Mask.toString(), dataSwitchNet2Mask.toString()];
    }

    function getIpv4RangeFromAddressAndNetmask(switchFabricCidr) {
      var part = switchFabricCidr.split("/"); // part[0] = base address, part[1] = netmask
      var ipaddress = part[0].split('.');
      var netmaskblocks = ["0","0","0","0"];
      if(!/\d+\.\d+\.\d+\.\d+/.test(part[1])) {
        // part[1] has to be between 0 and 32
        netmaskblocks = ("1".repeat(parseInt(part[1], 10)) + "0".repeat(32-parseInt(part[1], 10))).match(/.{1,8}/g);
        netmaskblocks = netmaskblocks.map(function(el) { return parseInt(el, 2); });
      } else {
        // xxx.xxx.xxx.xxx
        netmaskblocks = part[1].split('.').map(function(el) { return parseInt(el, 10) });
      }
      var invertedNetmaskblocks = netmaskblocks.map(function(el) { return el ^ 255; });
      var baseAddress = ipaddress.map(function(block, idx) { return block & netmaskblocks[idx]; });
      return baseAddress.join('.');
    }

    function normalize(a) {
        if (!_validate(a)) {
            throw new Error('Invalid address: ' + a);
        }
        a = a.toLowerCase()

        let nh = a.split(/\:\:/g);
        if (nh.length > 2) {
            throw new Error('Invalid address: ' + a);
        }

        let sections = [];
        if (nh.length == 1) {
            // full mode
            sections = a.split(/\:/g);
            if (sections.length !== 8) {
                throw new Error('Invalid address: ' + a);
            }
        } else if (nh.length == 2) {
            // compact mode
            let n = nh[0];
            let h = nh[1];
            let ns = n.split(/\:/g);
            let hs = h.split(/\:/g);
            for (let i in ns) {
                sections[i] = ns[i];
            }
            for (let i = hs.length; i > 0; --i) {
                sections[7 - (hs.length - i)] = hs[i - 1];
            }
        }
        for (let i = 0; i < 8; ++i) {
            if (sections[i] === undefined) {
                sections[i] = '0000';
            }
            sections[i] = _leftPad(sections[i], '0', 4);
        }
        return sections.join(':');
    };

    function abbreviate(a) {
        if (!_validate(a)) {
            throw new Error('Invalid address: ' + a);
        }
        a = normalize(a);
        a = a.replace(/0000/g, 'g');
        a = a.replace(/\:000/g, ':');
        a = a.replace(/\:00/g, ':');
        a = a.replace(/\:0/g, ':');
        a = a.replace(/g/g, '0');
        let sections = a.split(/\:/g);
        let zPreviousFlag = false;
        let zeroStartIndex = -1;
        let zeroLength = 0;
        let zStartIndex = -1;
        let zLength = 0;
        for (let i = 0; i < 8; ++i) {
            let section = sections[i];
            let zFlag = (section === '0');
            if (zFlag && !zPreviousFlag) {
                zStartIndex = i;
            }
            if (!zFlag && zPreviousFlag) {
                zLength = i - zStartIndex;
            }
            if (zLength > 1 && zLength > zeroLength) {
                zeroStartIndex = zStartIndex;
                zeroLength = zLength;
            }
            zPreviousFlag = (section === '0');
        }

        if (zPreviousFlag) {
            zLength = 8 - zStartIndex;
        }

        if (zLength > 1 && zLength > zeroLength) {
            zeroStartIndex = zStartIndex;
            zeroLength = zLength;
        }
        if (zeroStartIndex >= 0 && zeroLength > 1) {
            sections.splice(zeroStartIndex, zeroLength, 'g');
        }
        a = sections.join(':');
        a = a.replace(/\:g\:/g, '::');
        a = a.replace(/\:g/g, '::');
        a = a.replace(/g\:/g, '::');
        a = a.replace(/g/g, '::');

        return a;
    };

    // Basic validation
    function _validate(a) {
        return /^[a-f0-9\\:]+$/ig.test(a);
    };

    function _leftPad(d, p, n) {
        let padding = p.repeat(n);
        if (d.length < padding.length) {
            d = padding.substring(0, padding.length - d.length) + d;
        }
        return d;
    };

    function _hex2bin(hex) {
        return parseInt(hex, 16).toString(2)
    };

    function _bin2hex(bin) {
        return parseInt(bin, 2).toString(16)
    };

    function _addr2bin(addr) {
        let nAddr = normalize(addr);
        let sections = nAddr.split(":");
        let binAddr = '';
        for (let section of sections) {
            binAddr += _leftPad(_hex2bin(section), '0', 16);
        }
        return binAddr;
    };

    function _bin2addr(bin) {
        let addr = [];
        for (let i = 0; i < 8; ++i) {
            let binPart = bin.substr(i * 16, 16);
            let hexSection = _leftPad(_bin2hex(binPart), '0', 4);
            addr.push(hexSection);
        }
        return addr.join(':');
    };

    function getIpv6RangeFromCidr(addr, mask0, mask1, abbr) {
        if (!_validate(addr)) {
            throw new Error('Invalid address: ' + addr);
        }
        mask0 *= 1;
        mask1 *= 1;
        mask1 = mask1 || 128;
        if (mask0 < 1 || mask1 < 1 || mask0 > 128 || mask1 > 128 || mask0 > mask1) {
            throw new Error('Invalid masks.');
        }
        let binAddr = _addr2bin(addr);
        let binNetPart = binAddr.substr(0, mask0);
        let binHostPart = '0'.repeat(128 - mask1);
        let binStartAddr = binNetPart + '0'.repeat(mask1 - mask0) + binHostPart;
        let binEndAddr = binNetPart + '1'.repeat(mask1 - mask0) + binHostPart;
        if (!!abbr) {
            return {
                start: abbreviate(_bin2addr(binStartAddr)),
                end: abbreviate(_bin2addr(binEndAddr)),
                size: Math.pow(2, mask1 - mask0)
            };
        } else {
            return {
                start: _bin2addr(binStartAddr),
                end: _bin2addr(binEndAddr),
                size: Math.pow(2, mask1 - mask0)
            };
        }
    };

    function convertToHex(number) {

      if (number < 0) {
          number = 0xFFFFFFFF + number + 1;
      }

      return number.toString(16);
    }

    function convertZeroPadded(hexNumber,size) {
      var s = String(convertedNum);
      while (s.length < (size || 2)) {s = "0" + s;}
      return s;
    }

    function fillArrayWithFreeIps () {

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
        csfIpv6NetAddress = controlSwitchFabricCidr.split("/").slice(0)[0];
        csfIpv6NetMask = controlSwitchFabricCidr.split("/").slice(0)[1];
        initialCsfAddress = getIpv6RangeFromCidr(csfIpv6NetAddress,csfIpv6NetMask,"128").start;

        for (var iter = 2; iter < 24; iter++)
        {
          var csfIpSplit = initialCsfAddress.split(":");
          convertedNum= convertToHex(parseInt(csfIpSplit[7]) + iter);
          csfIpSplit[7] = convertZeroPadded(convertedNum, 4);
          freeCSFIps.push(csfIpSplit.join(':'));
        }

      }
      else if (controlSwitchFabricCidr.indexOf(".") !=-1){
        $.stack_params.cbam.resources.csfIpVersion = 4;
        $.stack_params.cbam.resources.csfDefaultGwIp = "0.0.0.0";
        firstFreeCSFIp = getIpv4RangeFromAddressAndNetmask(controlSwitchFabricCidr);

        for (var iter = 2; iter < 24; iter++)
        {
          var csfIpSplit = firstFreeCSFIp.split(".");
          csfIpSplit[3]= parseInt(csfIpSplit[3]) + iter;
          freeCSFIps.push(csfIpSplit.join('.'));
        }

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

      var tempMgIps = [];
      var tempLbIps = [];

      for(step in $.stack_params.cbam.resources.mgRedundantAspectGroup) {
         if(cCBAM.isNumeric(step)){
           if ($.resource_model.resources != undefined && !(cCBAM.operationIsUpgrade())) {
             if (step in $.resource_model.resources.mgRedundantAspectGroup.resources) {

                 var mgWorkingCSFip, mgProtectCSFip;

                 mgWorkingCSFip = $.resource_model.resources.mgRedundantAspectGroup.resources[step].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_csfIp;
                 mgProtectCSFip = $.resource_model.resources.mgRedundantAspectGroup.resources[step].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_csfIp;

                 tempMgIps.push(mgWorkingCSFip,mgProtectCSFip);

                 for (var i = 0; i < tempMgIps.length; i++) {
                   indexCSF = freeCSFIps.indexOf(tempMgIps[i]);

                   if (indexCSF > -1) {
                     freeCSFIps.splice(indexCSF, 1);
                   }
                 }
             }
           }

         }
      }

      for(step in $.stack_params.cbam.resources.loadBalancerAspectGroup) {
         if(cCBAM.isNumeric(step)){
           if ($.resource_model.resources != undefined && !(cCBAM.operationIsUpgrade())) {
             if (step in $.resource_model.resources.loadBalancerAspectGroup.resources) {

                 var lbCSFip;

                 lbCSFip = $.resource_model.resources.loadBalancerAspectGroup.resources[step].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_csfIp;

                 tempLbIps.push(lbCSFip);

                 for (var i = 0; i < tempLbIps.length; i++) {
                   indexCSF = freeCSFIps.indexOf(tempLbIps[i]);

                   if (indexCSF > -1) {
                     freeCSFIps.splice(indexCSF, 1);
                   }
                 }
             }
           }

         }
      }

      if ($.resource_model.resources != undefined && !(cCBAM.operationIsUpgrade())) {

        var oamACSFip, oamBCSFip, oamACSFIndex, oamBCSFIndex;

        oamACSFip = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMActive.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;
        oamBCSFip = $.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMStandby.resources.oamInstanceGroup.resources["0"].resources.oamServerInstance.metadata.nokia_vnf_csfIp;

        oamACSFIndex = freeCSFIps.indexOf(oamACSFip);
        if (oamACSFIndex > -1) {
          freeCSFIps.splice(oamACSFIndex, 1);

        }
        oamBCSFIndex = freeCSFIps.indexOf(oamBCSFip);
        if (oamBCSFIndex > -1) {
          freeCSFIps.splice(oamBCSFIndex, 1);
        }
      }

    }

    function assignMgSwitchFabricIps (resourceGroupIndex, mgWorkingCSFIp, mgProtectCSFIp) {

        $.stack_params.cbam.resources.mgRedundantAspectGroup[resourceGroupIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.controlSwitchFabricIp = mgWorkingCSFIp;
        $.stack_params.cbam.resources.mgRedundantAspectGroup[resourceGroupIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.controlSwitchFabricIp = mgProtectCSFIp;
        freeCSFIps.splice(0, 2);

    }

    function assignLbSwitchFabricIps (resourceGroupIndex, lbCSFIp) {

        $.stack_params.cbam.resources.loadBalancerAspectGroup[resourceGroupIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.controlSwitchFabricIp = lbCSFIp;
        freeCSFIps.splice(0, 1);

    }
}

function classCBAM() {
    //declare public members
    this.operationIsInstantiation = operationIsInstantiation;
    this.operationIsScaling       = operationIsScaling;
    this.operationIsHealing       = operationIsHealing;
    this.operationIsUpgrade       = operationIsUpgrade;
    this.operationIsReInstantiate = operationIsReInstantiate;
    this.isNumeric = isNumeric;
    this.assignBlacklistedGroups = assignBlacklistedGroups;

    function isNumeric(n) {
       return !isNaN(parseFloat(n)) && isFinite(n)
    }

    function operationIsInstantiation() {

        if ($.operation_params.instantiationLevelId != undefined) {
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
    function operationIsReInstantiate() {

        if ($.operation_params.additionalParams != undefined) {
            if ($.operation_params.additionalParams.vnfcToReInstantiate != undefined) {
                return true;
            }
        }

        return false;
    }
    function operationIsUpgrade() {

        if (!operationIsInstantiation() && !operationIsScaling() && !operationIsHealing() && !operationIsReInstantiate()) {
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
}
