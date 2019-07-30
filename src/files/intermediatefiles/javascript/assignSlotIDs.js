// This is required for Liberty as it does not convert list to array
var str = $.stack_params.cbam.extensions.externalIpAddressList;
$.stack_params.cbam.extensions.externalIpAddressList = str.split(',');

// These should be inline with TOSCA *until we find a way to read them dynamically---
//LB
var min_number_of_instances_lb = 2; //capabilities::deployment_flavour::properties::vdu_profile::loadBalancerServer::min_number_of_instances
var max_scale_level_lb = 8;         //capabilities::deployment_flavour::properties::scaling_aspects::loadBalancerAspect::max_scale_level
//MG
var min_number_of_instances_mg = 1; //capabilities::deployment_flavour::properties::vdu_profile::mgServer::min_number_of_instances
var max_scale_level_mg = 9;         //capabilities::deployment_flavour::properties::scaling_aspects::mgRedundantAspect::max_scale_level
// --- These should be inline with TOSCA
// conf
var reserveLBSlots=false;
var useLbECPs=true;
var useMgECPs=false;
var debugLog=false;
// init
var cCBAM = new classCBAM();
var cLog = new classLog();
var cSlot = new classSlotID();
var cECPs = new classECP();
var cAnsibleCheckVMs = new classAnsibleCheckVMs();

var lbRgIndexes = cCBAM.getLbRgIndexes();
var mgRgIndexes = cCBAM.getMgRgIndexes();

//main
if (cCBAM.operationIsInstantiation() || cCBAM.operationIsHealing() || cCBAM.operationIsUpgrade() || cCBAM.operationIsReInstantiate())
{
    var slotIDmarker = cSlot.firstPossibleSlotIDforLB();
    var slotID = -1;

    //LBs
    for (var iter = 0; iter < lbRgIndexes.length; iter++)
    {
        if (cCBAM.operationIsInstantiation()){
            slotID = slotIDmarker++;
        }
        else {//Heal/Upgrade
          if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              slotID = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
          }
          else {
              slotID = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
          }
        }

        cSlot.assignToLB(lbRgIndexes[iter], slotID);
        cECPs.assignToLB(lbRgIndexes[iter], slotID);

        cCBAM.assignVmPresence("LB", lbRgIndexes[iter]);
        cAnsibleCheckVMs.addSlot(slotID);
    }

    //if LB Slots are reserved
    if (reserveLBSlots) {
        slotIDmarker = cSlot.firstPossibleSlotIDforMG();
    }

    //MGs
    for (var iter = 0; iter < mgRgIndexes.length; iter++)
    {
        //mgWorking
        if (cCBAM.operationIsInstantiation()){
            slotID = slotIDmarker++;
        }
        else {//Heal/Upgrade
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()) {
              slotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_slotId;
            }
            else {
              slotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
            }
        }
        cSlot.assignToMgWorking(mgRgIndexes[iter], slotID);
        cECPs.assignToMgWorking(mgRgIndexes[iter]);
        cAnsibleCheckVMs.addSlot(slotID);

        //mgProtect
        if (cCBAM.operationIsInstantiation()){
            slotID = slotIDmarker++;
        }
        else { //Heal/Upgrade
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              slotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_slotId;
            } else {
              slotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
            }
        }
        cSlot.assignToMgProtect(mgRgIndexes[iter], slotID);
        cECPs.assignToMgProtect(mgRgIndexes[iter]);

        cCBAM.assignVmPresence("MG", mgRgIndexes[iter]);
        cAnsibleCheckVMs.addSlot(slotID);
    }

    cAnsibleCheckVMs.addSlot("A");
    cAnsibleCheckVMs.addSlot("B");
}
else if (cCBAM.operationIsScaling())
{

    var mgScaleWorkingSlotId = [];
    var mgScaleProtectSlotId = [];
    var mgGroupIdAnsible = [];
    var lbScaleSlotId = [];
    var lbGroupIdAnsible = [];

    if (cCBAM.operationIsScaleOUT())
    {
        //find existing slot IDs assigned to LBs and MGs
        cSlot.initializeArrayOfExistingSlotIDs();

        //LBs
        for (var iter = 0; iter < lbRgIndexes.length; iter++)
        {
            var lbSlotId = 0;

            if (iter >= (lbRgIndexes.length - cCBAM.numberofScalingStepsLB())) // if resource not yet exist
            {
                //try to find next available slotID for LB
                lbSlotId = cSlot.findNextFreeSlotIDforLB();

                //add slotID for ansible checkVM
                cAnsibleCheckVMs.addSlot(lbSlotId);

                //fill arrays with LB scaled node slot ids for ansible
                lbScaleSlotId.push(parseInt(lbSlotId));

                var candidateGroupId = parseInt($.stack_params.cbam.resources.loadBalancerAspectGroup[lbRgIndexes[iter]]._mappedIndex) + 1;
                lbGroupIdAnsible.push(parseInt(candidateGroupId));
            }
            else
            {
                //read x.slotID from deployed resource
                if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
                  lbSlotId = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
                } else {
                  lbSlotId = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
                }
            }
            cSlot.assignToLB(lbRgIndexes[iter], lbSlotId);
            cECPs.assignToLB(lbRgIndexes[iter], lbSlotId);
            cCBAM.assignVmPresence("LB", lbRgIndexes[iter]);

        }
        //MGs
        for (var iter = 0; iter < mgRgIndexes.length; iter++)
        {

            var workingSlotID = 0;
            var protectSlotID = 0;

            if (iter >= (mgRgIndexes.length - cCBAM.numberofScalingStepsMG())) // if resources not yet exist
            {
                var mgSlotIDsObject = cSlot.findNextFreeSlotIDsforMG();

                workingSlotID  = mgSlotIDsObject.workingSlot;
                protectSlotID  = mgSlotIDsObject.protectSlot;

                //add for ansible checkVM
                cAnsibleCheckVMs.addSlot(workingSlotID);
                cAnsibleCheckVMs.addSlot(protectSlotID);

                //fill arrays with MG scaled node slot ids for ansible
                mgScaleWorkingSlotId.push(parseInt(workingSlotID));
                mgScaleProtectSlotId.push(parseInt(protectSlotID));

                var candidateGroupId = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]]._mappedIndex) + 1;
                mgGroupIdAnsible.push(parseInt(candidateGroupId));

            }
            else
            {
                //read slotID from deployed resource
                if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
                  workingSlotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_slotId;
                  protectSlotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_slotId;
                }
                else {
                  workingSlotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
                  protectSlotID = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
                }
            }

            //mgWorking
            cSlot.assignToMgWorking(mgRgIndexes[iter], workingSlotID);
            cECPs.assignToMgWorking(mgRgIndexes[iter]);

            //mgProtect
            cSlot.assignToMgProtect(mgRgIndexes[iter], protectSlotID);
            cECPs.assignToMgProtect(mgRgIndexes[iter]);

            cCBAM.assignVmPresence("MG", mgRgIndexes[iter]);

        }

        //add MG-LB scaling group ids and MG-LB scaled node slot ids for ansible

        cCBAM.assignMgScalingIds();
        cCBAM.assignLbScalingIds();

    }
    else if (cCBAM.operationIsScaleIN())
    {

        //read slotIDs from deployed resources
        for (var iter = 0; iter < lbRgIndexes.length; iter++)
        {
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              var existingLbSlotId = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
            }
            else {
              var existingLbSlotId = $.resource_model.resources.loadBalancerAspectGroup.resources[lbRgIndexes[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId;
            }

            cSlot.assignToLB(lbRgIndexes[iter], existingLbSlotId);
            cECPs.assignToLB(lbRgIndexes[iter], existingLbSlotId);
            cCBAM.assignVmPresence("LB", lbRgIndexes[iter]);

        }

        for (var iter = 0; iter < mgRgIndexes.length; iter++)
        {
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              var workingSlot = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_slotId;
              var protectSlot = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_slotId;
            }
            else {
              var workingSlot = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
              var protectSlot = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId;
            }

            //mgWorking
            cSlot.assignToMgWorking(mgRgIndexes[iter],workingSlot);
            cECPs.assignToMgWorking(mgRgIndexes[iter]);

            //mgProtect
            cSlot.assignToMgProtect(mgRgIndexes[iter],protectSlot);
            cECPs.assignToMgProtect(mgRgIndexes[iter]);

            cCBAM.assignVmPresence("MG", mgRgIndexes[iter]);

        }

        cCBAM.assignMgScalingIds();
        cCBAM.assignLbScalingIds();

    }
    else
    {
        throw new Error('Invalid Scaling operation');
    }

}
else //no valid operation
{
    var opType="empty";

    if ($.operation_params.type != undefined) {
        opType = $.operation_params.type.toString();
    }

    throw new Error('Unknown operation type:' + opType);
}

$.stack_params.cbam.resources.slotIdAnsible = cAnsibleCheckVMs.getList().toString();
$.stack_params.cbam.resources.oamAspectGroup.count = 1;
cCBAM.assignOAMIps();
cCBAM.assignVmPresence("OAM", "0");
return $.stack_params;

//
//Supplementary Functions
//

function classCBAM() {
    //declare public members
    this.operationIsInstantiation = operationIsInstantiation;
    this.operationIsScaling       = operationIsScaling;
    this.operationIsHealing       = operationIsHealing;
    this.operationIsUpgrade       = operationIsUpgrade;
    this.operationIsReInstantiate = operationIsReInstantiate;
    this.operationIsScaleOUT      = operationIsScaleOUT;
    this.operationIsScaleIN       = operationIsScaleIN;
    this.differenceOfGroupIndexes          = differenceOfGroupIndexes;
    this.isNumeric     = isNumeric;
    this.checkNested   = checkNested;
    this.numberofScalingStepsLB          = numberofScalingStepsLB;
    this.numberofScalingStepsMG          = numberofScalingStepsMG;
    this.getMaximumAllowedVMs     = getMaximumAllowedVMs;
    this.assignMgScalingIds         = assignMgScalingIds;
    this.assignLbScalingIds         = assignLbScalingIds;
    this.assignOAMIps            = assignOAMIps;
    this.getLbRgIndexes          = getLbRgIndexes;
    this.getMgRgIndexes          = getMgRgIndexes;
    this.instantiatedPackageIsOld  = instantiatedPackageIsOld;
    this.assignVmPresence        = assignVmPresence;

    //private variables
    var max_number_of_allowed_VMs = 20;

    //constructor
    function init() {

        convertCountToNumber();
        checkMinimumScalingLevel();
        checkMaximumScalingLevel();
        checkMtuRange();
        checkCupsLBscaling();

    }
    init();

    //private functions
    function getMaximumAllowedVMs() {  return max_number_of_allowed_VMs;   }

    function checkMinimumScalingLevel() {

        if (operationIsScaleIN()) {

            if ($.operation_params.aspectId == "loadBalancerAspect")
            {
                if ($.resource_model.resources.loadBalancerAspectGroup != undefined) {
                    if ($.resource_model.resources.loadBalancerAspectGroup.resources != undefined) {
                        var _currentLbGroups = Object.keys($.resource_model.resources.loadBalancerAspectGroup.resources).length;

                        if ($.operation_params.numberOfSteps != undefined) {

                            var numberofSteps = parseInt($.operation_params.numberOfSteps);

                            var targetLbGroups = _currentLbGroups - numberofSteps;
                            if (targetLbGroups < min_number_of_instances_lb)
                            {
                                throw new Error('Cannot scale IN LB Groups below:' + min_number_of_instances_lb);
                            }
                        }
                    }
                }
            }
            else if ($.operation_params.aspectId == "mgRedundantAspect")
            {
                if ($.resource_model.resources.mgRedundantAspectGroup != undefined) {
                    if ($.resource_model.resources.mgRedundantAspectGroup.resources != undefined) {
                        var _currentMgGroups = Object.keys($.resource_model.resources.mgRedundantAspectGroup.resources).length;

                        if ($.operation_params.numberOfSteps != undefined) {

                            var numberofSteps = parseInt($.operation_params.numberOfSteps);

                            var targetMgGroups = _currentMgGroups - numberofSteps;
                            if (targetMgGroups < min_number_of_instances_mg)
                            {
                                throw new Error('Cannot scale IN MG Groups below:' + min_number_of_instances_mg);
                            }
                        }
                    }
                }
            }
        }//if (operationIsScaleIN()) {
    }//checkMinimumScalingLevel

    function checkMaximumScalingLevel()
    {
        //calculate Scale Level for MG
        var scaleLevelMg = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup.count);
        if (scaleLevelMg > max_scale_level_mg) // max_sale_level defined in vnfd
        {
            throw new Error('unable to scale out beyond maximum level for MG which is ' + max_scale_level_mg);
        }

        // Scale Level for LoadBalancer
        var scaleLevelLb = parseInt($.stack_params.cbam.resources.loadBalancerAspectGroup.count);
        if (scaleLevelLb > max_scale_level_lb) // max_sale_level defined in vnfd
        {
            throw new Error('unable to scale out beyond maximum level for LB which is ' + max_scale_level_lb);
        }

        //Check Maximim VMs reached
        var totalNumberVms = scaleLevelLb + (scaleLevelMg * 2);
        if (totalNumberVms > max_number_of_allowed_VMs)
        {
            throw new Error('Total Number('+totalNumberVms+') of MG/LB VMs is more than 20');
        }
    }

    function checkMtuRange() {
        if (($.stack_params.cbam.extensions.mtu < 1400) || ($.stack_params.cbam.extensions.mtu > 9000)) {
            throw new Error('MTU size value is not within the expected range');
        }
    }

    function checkCupsLBscaling() {
        if (($.stack_params.cbam.extensions.cupsMode == true) && ($.operation_params.aspectId == "loadBalancerAspect")) {
            throw new Error('Scaling LB nodes is not allowed when CUPS mode is enabled');
        }
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

    function instantiatedPackageIsOld() {

        if($.resource_model.resources.oamAspectGroup.resources["0"].resources.OAMActive.resources.oamInstanceGroup == undefined) {
            return true;
        }
        return false;
    }

    function operationIsScaleOUT() {
        if (operationIsScaling()) {
            return ($.operation_params.type.toLowerCase() == "out");
        }
        return false;
    }
    function operationIsScaleIN() {
        if (operationIsScaling()) {
            return ($.operation_params.type.toLowerCase() == "in");
        }
        return false;
    }

    function numberofScalingStepsLB()  {

        if ($.operation_params.aspectId == "loadBalancerAspect") {
            var numberofSteps = parseInt($.operation_params.numberOfSteps);
            return numberofSteps;
        }

        return 0;
    }
    function numberofScalingStepsMG()  {

        if ($.operation_params.aspectId == "mgRedundantAspect") {
            var numberofSteps = parseInt($.operation_params.numberOfSteps);
            return numberofSteps;
        }

        return 0;
    }

    function differenceOfGroupIndexes (existingIndexes, scaledIndexes) {

        var temp = [];
        existingIndexes = existingIndexes.toString().split(',').map(Number);
        scaledIndexes = scaledIndexes.toString().split(',').map(Number);

        for (var i in existingIndexes) {
            if(scaledIndexes.indexOf(existingIndexes[i]) === -1) temp.push(existingIndexes[i]);
        }
        for(i in scaledIndexes) {
           if(existingIndexes.indexOf(scaledIndexes[i]) === -1) temp.push(scaledIndexes[i]);
        }
        return temp.sort((a,b) => a-b);
    }

    function assignOAMIps() {

        if ( $.stack_params.cbam.extensions.oamActiveVirtualIp == "" && $.stack_params.cbam.extensions.oamStandbyVirtualIp == "" ) {
            $.stack_params.cbam.resources.oamActiveManagementIp = $.stack_params.cbam.externalConnectionPoints.oamManagementECP.addresses[0].ip;
            $.stack_params.cbam.resources.oamStandbyManagementIp = $.stack_params.cbam.externalConnectionPoints.oamManagementECP.addresses[1].ip;
            $.stack_params.cbam.resources.oamAllowedAddressPairsA = $.stack_params.cbam.externalConnectionPoints.oamManagementECP.addresses[1].ip;
            $.stack_params.cbam.resources.oamAllowedAddressPairsB = $.stack_params.cbam.externalConnectionPoints.oamManagementECP.addresses[0].ip;
        }
        else {
            $.stack_params.cbam.resources.oamActiveManagementIp = $.stack_params.cbam.extensions.oamActiveVirtualIp;
            $.stack_params.cbam.resources.oamStandbyManagementIp = $.stack_params.cbam.extensions.oamStandbyVirtualIp;
            $.stack_params.cbam.resources.oamAllowedAddressPairsA = $.stack_params.cbam.extensions.oamActiveVirtualIp + "," + $.stack_params.cbam.extensions.oamStandbyVirtualIp;
            $.stack_params.cbam.resources.oamAllowedAddressPairsB = $.stack_params.cbam.extensions.oamStandbyVirtualIp + "," + $.stack_params.cbam.extensions.oamActiveVirtualIp;
        }

        var oamAllowedAddressPairsA = $.stack_params.cbam.resources.oamAllowedAddressPairsA;
        var oamAllowedAddressPairsB = $.stack_params.cbam.resources.oamAllowedAddressPairsB;
        $.stack_params.cbam.resources.oamAllowedAddressPairsA = oamAllowedAddressPairsA.split(",");
        $.stack_params.cbam.resources.oamAllowedAddressPairsB = oamAllowedAddressPairsB.split(",");

        return 0;
    }

    function isNumeric(n) {
      return !isNaN(parseFloat(n)) && isFinite(n)
    }

    function checkNested(obj /*, level1, level2, ... levelN*/) {
      var args = Array.prototype.slice.call(arguments, 1);

      for (var i = 0; i < args.length; i++) {
        if (!obj || !obj.hasOwnProperty(args[i])) {
          return false;
        }
        obj = obj[args[i]];
      }
      return true;
    }

    function getLbRgIndexes() {
      var tempLbIndexArray = [];
      for( step in $.stack_params.cbam.resources.loadBalancerAspectGroup ) {
         if (isNumeric(step)) {
            tempLbIndexArray.push(step)
         }
      }
      tempLbIndexArray.sort(function(a, b){return a - b});
      return tempLbIndexArray;
    }

    function getMgRgIndexes() {
      var tempMgIndexArray = [];
      for( step in $.stack_params.cbam.resources.mgRedundantAspectGroup ) {
         if (isNumeric(step)) {
            tempMgIndexArray.push(step)
         }
      }
      tempMgIndexArray.sort(function(a, b){return a - b});
      return tempMgIndexArray;
    }

    function assignMgScalingIds () {

      var tempMgExistingGroupIndexes = [];
      var tempMgGroupIndexesAfterScaleIn = [];
      var tempScaleInMgGroupIds = [];

      if (operationIsScaleIN()) {
        for( step in $.resource_model.resources.mgRedundantAspectGroup.resources ) {
            if (isNumeric(step)) {
               tempMgExistingGroupIndexes.push(step)
            }
        }

        for( step in $.stack_params.cbam.resources.mgRedundantAspectGroup ) {
            if (isNumeric(step)) {
                tempMgGroupIndexesAfterScaleIn.push(step)
            }
        }

        tempScaleInMgGroupIds= differenceOfGroupIndexes(tempMgExistingGroupIndexes, tempMgGroupIndexesAfterScaleIn);

        for ( var iter = 0; iter < tempScaleInMgGroupIds.length; iter++ ) {

          if (checkNested($.resource_model, 'resources', 'mgRedundantAspectGroup', 'resources', tempScaleInMgGroupIds[iter], 'resources', 'mgWorking', 'resources', 'mgInstanceGroup', 'resources', '0', 'resources', 'mgServerInstance', 'metadata') && checkNested($.resource_model, 'resources', 'mgRedundantAspectGroup', 'resources', tempScaleInMgGroupIds[iter], 'resources', 'mgProtect', 'resources', 'mgInstanceGroup', 'resources', '0', 'resources', 'mgServerInstance', 'metadata')) {

            mgScaleWorkingSlotId.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempScaleInMgGroupIds[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId));
            mgScaleProtectSlotId.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempScaleInMgGroupIds[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId));
            var mgCandidateGroupId = parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempScaleInMgGroupIds[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_groupIndex);
            mgGroupIdAnsible.push(parseInt(mgCandidateGroupId)+1);
          }
        }
      }

      if ( mgScaleWorkingSlotId.length > 0 && mgScaleProtectSlotId.length > 0 ) {

        $.stack_params.cbam.resources.mgGroupIdAnsible = "['" + mgGroupIdAnsible.join("','") + "']";
        $.stack_params.cbam.resources.mgScaleWorkingSlotId = "['" + mgScaleWorkingSlotId.join("','") + "']";
        $.stack_params.cbam.resources.mgScaleProtectSlotId = "['" + mgScaleProtectSlotId.join("','") + "']";

        if (operationIsScaleOUT()) {

          $.stack_params.cbam.resources.mgScaleOutOperation = null;
        }
        else {

          $.stack_params.cbam.resources.mgScaleInOperation = null;
        }
      }
      return true;
    }

    function assignLbScalingIds () {

      var tempLbExistingGroupIndexes = [];
      var tempLbGroupIndexesAfterScaleIn = [];
      var tempScaleInLbGroupIds = [];

      if (operationIsScaleIN()) {
        for ( step in $.resource_model.resources.loadBalancerAspectGroup.resources ) {
            if (isNumeric(step)){
                tempLbExistingGroupIndexes.push(step)
            }
        }

        for(step in $.stack_params.cbam.resources.loadBalancerAspectGroup) {
            if (isNumeric(step)){
                tempLbGroupIndexesAfterScaleIn.push(step)
            }
        }

        tempScaleInLbGroupIds = differenceOfGroupIndexes(tempLbExistingGroupIndexes, tempLbGroupIndexesAfterScaleIn);

        for ( var iter = 0; iter < tempScaleInLbGroupIds.length; iter++ ) {
          if (checkNested($.resource_model, 'resources', 'loadBalancerAspectGroup', 'resources', tempScaleInLbGroupIds[iter], 'resources', 'loadBalancerInstanceGroup', 'resources', '0', 'resources', 'loadBalancerServerInstance', 'metadata')) {

            lbScaleSlotId.push(parseInt($.resource_model.resources.loadBalancerAspectGroup.resources[tempScaleInLbGroupIds[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId));
            var lbCandidateGroupId = parseInt($.resource_model.resources.loadBalancerAspectGroup.resources[tempScaleInLbGroupIds[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_groupIndex);
            lbGroupIdAnsible.push(parseInt(lbCandidateGroupId)+1);
          }
        }
      }

      if ( lbScaleSlotId.length > 0 ) {
          $.stack_params.cbam.resources.lbGroupIdAnsible = "['" + lbGroupIdAnsible.join("','") + "']";
          $.stack_params.cbam.resources.lbScaleSlotId = "['" + lbScaleSlotId.join("','") + "']";

          if (operationIsScaleOUT()) {

              $.stack_params.cbam.resources.lbScaleOutOperation = null;
          }
          else {
              $.stack_params.cbam.resources.lbScaleInOperation = null;
          }
      }
      return true;
    }

    function assignVmPresence(nodeType, rgIndex) {

      if (nodeType == "LB") {
          $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].present = 1;
      }
      else if (nodeType == "MG") {
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.present = 1;
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.present = 1;
      }
      else if (nodeType == "OAM") {
          $.stack_params.cbam.resources.oamAspectGroup[rgIndex].OAMActive.present = 1;
          $.stack_params.cbam.resources.oamAspectGroup[rgIndex].OAMStandby.present = 1;
      }
      else {
          throw new Error("Invalid node type was provided");
      }

      return 0;
    }

    function convertCountToNumber() {

      lbGroupCount = Number($.stack_params.cbam.resources.loadBalancerAspectGroup.count);
      mgGroupCount = Number($.stack_params.cbam.resources.mgRedundantAspectGroup.count);
      $.stack_params.cbam.resources.loadBalancerAspectGroup.count = lbGroupCount;
      $.stack_params.cbam.resources.mgRedundantAspectGroup.count = mgGroupCount;

      return 0;
    }

};

//Slot IDs
function classSlotID() {

    //declare public members
    this.clearArrayOfSlotIds= clearArrayOfSlotIds;
    this.firstPossibleSlotIDforLB= firstPossibleSlotIDforLB;
    this.lastPossibleSlotIDforLB= lastPossibleSlotIDforLB;
    this.firstPossibleSlotIDforMG= firstPossibleSlotIDforMG;
    this.lastPossibleSlotIDforMG= lastPossibleSlotIDforMG;
    this.checkIfValidSlotIdforMG= checkIfValidSlotIdforMG;
    this.checkIfValidSlotIdforLB= checkIfValidSlotIdforLB;
    this.findNextFreeSlotIDforLB= findNextFreeSlotIDforLB;
    this.findNextFreeSlotIDsforMG= findNextFreeSlotIDsforMG;
    this.initializeArrayOfExistingSlotIDs= initializeArrayOfExistingSlotIDs;
    this.assignToLB     = assignToLB;
    this.assignToMgWorking= assignToMgWorking;
    this.assignToMgProtect= assignToMgProtect;

    //private variables
    var arrayOfSlotIds = new Array();

    //private functions
    function clearArrayOfSlotIds() {

        //arrayOfSlotIds = [];
        //return;

        while (arrayOfSlotIds.length) {
            arrayOfSlotIds.pop();
        }
    }

    function firstPossibleSlotIDforLB()
    {
        return 1;
    }
    function lastPossibleSlotIDforLB()
    {
        if (reserveLBSlots)
        {
            return  max_scale_level_lb;
        }
        else
        {
            return cCBAM.getMaximumAllowedVMs();
        }
    }
    function firstPossibleSlotIDforMG()
    {
        if (reserveLBSlots)
        {
            return (max_scale_level_lb+1);
        }
        else
        {
            return 1;
        }
    }
    function lastPossibleSlotIDforMG()
    {
        return cCBAM.getMaximumAllowedVMs();
    }

    function checkIfValidSlotIdforMG(slotID)
    {

        if ((slotID >= firstPossibleSlotIDforMG()) && (slotID <= lastPossibleSlotIDforMG())) {
            //all ok
        }
        else {
            throw new Error('Invalid MG slotID:' + slotID);
        }
    }
    function checkIfValidSlotIdforLB(slotID)
    {

        if ((slotID >= firstPossibleSlotIDforLB()) && (slotID <= lastPossibleSlotIDforLB())) {
            //all ok
        }
        else {
            throw new Error('Invalid LB slotID:' + slotID);
        }
    }

    //
    // Find next 1 free slotIDs for LB
    //
    // Return:
    //      lbSlotID
    //
    function findNextFreeSlotIDforLB()
    {
        var lbSlot = 0;

        for (var iter = 1; iter <= lastPossibleSlotIDforLB(); iter++)
        {
            if (arrayOfSlotIds.indexOf(iter) == -1) //this slot not used until now
            {
                lbSlot = iter;//found it
                arrayOfSlotIds.push(parseInt(lbSlot));//add it to list of used slotIDs
                iter = (lastPossibleSlotIDforLB()+1);//break loop
            }
        }
        //did we find 1 slotids
        if ((lbSlot == 0))
        {
            //we havent found free slotID
            throw new Error('Cannot find free slotID for LB');
        }
        //sanity check
        checkIfValidSlotIdforLB(lbSlot);

        return lbSlot;
    }
    //
    // Find next 2 free slotIDs for MG
    //
    // Return:
    //      obj.workingSlot
    //      obj.protectSlot
    function findNextFreeSlotIDsforMG()
    {
        var workingSlot = 0;
        var protectSlot = 0;

        for (var iter = firstPossibleSlotIDforMG(); iter <= lastPossibleSlotIDforMG(); iter++)
        {
            if (arrayOfSlotIds.indexOf(iter) == -1) //this slot is not used until now
            {
                if (workingSlot == 0) {
                    workingSlot = iter;
                }
                else if (protectSlot == 0) {
                    protectSlot = iter;
                }

                //add it to list of used slotIDs
                arrayOfSlotIds.push(parseInt(iter));

                //if both are found break loop
                if ((workingSlot != 0) && (protectSlot != 0)) {
                   iter = (lastPossibleSlotIDforMG()+1);//break loop
                }
            }
        }
        //did we find 2 slotids ?
        if ((workingSlot == 0) || (protectSlot == 0))
        {
            //we havent found free slotID for MG
            throw new Error('Cannot find free slotIDs for MG');
        }

        //sanity check
        checkIfValidSlotIdforMG(workingSlot);
        checkIfValidSlotIdforMG(protectSlot);

        return {
            workingSlot: workingSlot,
            protectSlot: protectSlot
        };

    }

    function initializeArrayOfExistingSlotIDs() {

        //reset array but keep object
        clearArrayOfSlotIds();

        tempLbIndexesArray = cCBAM.getLbRgIndexes();
        currentLbLength = tempLbIndexesArray.length;
        tempLbIndexesArray.length = currentLbLength - cCBAM.numberofScalingStepsLB();

        tempMgIndexesArray = cCBAM.getMgRgIndexes();
        currentMgLength = tempMgIndexesArray.length;
        tempMgIndexesArray.length = currentMgLength - cCBAM.numberofScalingStepsMG();

        for (var iter = 0; iter < tempLbIndexesArray.length; iter++)
        {
            tempLbIndex = tempLbIndexesArray[iter];
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              arrayOfSlotIds.push(parseInt($.resource_model.resources.loadBalancerAspectGroup.resources[tempLbIndex].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId));
            } else {
              arrayOfSlotIds.push(parseInt($.resource_model.resources.loadBalancerAspectGroup.resources[tempLbIndex].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId));
            }
        }
        //find existing slot IDs assigned to MGs
        for (var iter = 0; iter < tempMgIndexesArray.length; iter++)
        {
            tempMgIndex = tempMgIndexesArray[iter];
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              arrayOfSlotIds.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempMgIndex].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_slotId));
              arrayOfSlotIds.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempMgIndex].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_slotId));
            }
            else {
              arrayOfSlotIds.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempMgIndex].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId));
              arrayOfSlotIds.push(parseInt($.resource_model.resources.mgRedundantAspectGroup.resources[tempMgIndex].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId));
            }

        }
    }
    function assignToLB(rgIndex, slotID)
    {
        checkIfValidSlotIdforLB(slotID);
        $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.slot = slotID.toString();
    }
    function assignToMgWorking(rgIndex, slotID)
    {
        checkIfValidSlotIdforMG(slotID);
        $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.slot = slotID.toString();
    }
    function assignToMgProtect(rgIndex, slotID)
    {
        checkIfValidSlotIdforMG(slotID);
        $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.slot = slotID.toString();
    }

};

function classLog() {
    //declare public members
    this.log            = log;
    this.getLog         = getLog;
    this.throwLog       = throwLog;

    //private variables
    var buffer  = "";
    var prefix = "mgDebugLog";
    var seperator = '#';

    //private functions
    function log(text)
    {
        if (!debugLog)
            return;
        var tmp = buffer + seperator + text;
        buffer = tmp;
    }

    function getLog()
    {
        if (!debugLog)
            return '';

        return buffer;
    }

    function throwLog()
    {
        if (!debugLog)
            return;

        throw new Error(prefix + ':' + buffer);
    }
};

function classAnsibleCheckVMs() {

    //declare public members
    this.addSlot            = addSlot;
    this.getList            = getList;

    //private variables
    var slotIdAnsible = [];

    //private functions
    function addSlot(slotID)
    {
        slotIdAnsible.push(slotID);
    }

    function getList()
    {
        slotIdAnsible.sort(function(a, b){return a - b});
        if (slotIdAnsible.length > 0) {
           var ret = "['" + slotIdAnsible.join("','") + "']";
        }
        else {
           var ret = "[" + slotIdAnsible + "]";
        }
        return ret;
    }

};

function classECP() {

    //declare public members
    this.assignToLB         = assignToLB;
    this.assignToMgWorking  = assignToMgWorking;
    this.assignToMgProtect  = assignToMgProtect;

    //private functions

    function assignToLB(rgIndex)
    {
        if (!useLbECPs)
            return;

        var mappedIndex = parseInt($.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex]._mappedIndex);
        var ecp1Index = parseInt($.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex]._mappedIndex) + 1;
        var ecp2Index = parseInt($.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex]._mappedIndex) + 2;

        if ($.resource_model.resources != undefined) {
          if (rgIndex in $.resource_model.resources.loadBalancerAspectGroup.resources) {
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp1 = $.resource_model.resources.loadBalancerAspectGroup.resources[rgIndex].resources.loadBalancerServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp2 = $.resource_model.resources.loadBalancerAspectGroup.resources[rgIndex].resources.loadBalancerServerInstance.metadata.nokia_vnf_ecp2;
            }
            else {
              $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp1 = $.resource_model.resources.loadBalancerAspectGroup.resources[rgIndex].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp2 = $.resource_model.resources.loadBalancerAspectGroup.resources[rgIndex].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_ecp2;
            }
          }
          else {
            $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp1 = "loadBalancerExt" + (mappedIndex + ecp1Index).toString() + "ECP";
            $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp2 = "loadBalancerExt" + (mappedIndex + ecp2Index).toString() + "ECP";
          }
        }
        else {
            $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp1 = "loadBalancerExt" + (mappedIndex + ecp1Index).toString() + "ECP";
            $.stack_params.cbam.resources.loadBalancerAspectGroup[rgIndex].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.ecp2 = "loadBalancerExt" + (mappedIndex + ecp2Index).toString() + "ECP";
        }
    }
    function assignToMgWorking(rgIndex)
    {
        if (!useMgECPs)
            return;

        var mappedIndex = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex);
        var ecp1Index = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex) + 1;
        var ecp2Index = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex) + 2;

        if ($.resource_model.resources != undefined) {
          if (rgIndex in $.resource_model.resources.mgRedundantAspectGroup.resources) {
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp1 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp2 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgWorking.resources.mgServerInstance.metadata.nokia_vnf_ecp2;
            } else {
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp1 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp2 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_ecp2;
            }
          }
          else {
            $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp1 = "mgWorkingExt" + (mappedIndex + ecp1Index).toString() + "ECP";
            $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp2 = "mgWorkingExt" + (mappedIndex + ecp2Index).toString() + "ECP";
          }
        }
        else {
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp1 = "mgWorkingExt" + (mappedIndex + ecp1Index).toString() + "ECP";
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgWorking.mgInstanceGroup["0"].mgServerInstance.ecp2 = "mgWorkingExt" + (mappedIndex + ecp2Index).toString() + "ECP";
        }
    }
    function assignToMgProtect(rgIndex)
    {
        if (!useMgECPs)
            return;

        var mappedIndex = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex);
        var ecp1Index = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex) + 1;
        var ecp2Index = parseInt($.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex]._mappedIndex) + 2;

        if ($.resource_model.resources != undefined) {
          if (rgIndex in $.resource_model.resources.mgRedundantAspectGroup.resources) {
            if (cCBAM.operationIsUpgrade() && cCBAM.instantiatedPackageIsOld()){
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp1 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp2 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgProtect.resources.mgServerInstance.metadata.nokia_vnf_ecp2;
            } else {
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp1 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_ecp1;
              $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp2 = $.resource_model.resources.mgRedundantAspectGroup.resources[rgIndex].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_ecp2;
          }
          }
          else {
            $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp1 = "mgProtectExt" + (mappedIndex + ecp1Index).toString() + "ECP";
            $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp2 = "mgProtectExt" + (mappedIndex + ecp2Index).toString() + "ECP";
          }
        }
        else {
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp1 = "mgProtectExt" + (mappedIndex + ecp1Index).toString() + "ECP";
          $.stack_params.cbam.resources.mgRedundantAspectGroup[rgIndex].mgProtect.mgInstanceGroup["0"].mgServerInstance.ecp2 = "mgProtectExt" + (mappedIndex + ecp2Index).toString() + "ECP";
        }
    }

};
