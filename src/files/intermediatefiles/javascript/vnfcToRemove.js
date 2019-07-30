aux_params = $.auxiliary_params || {}
function findScalingStepIdByVnfcId(vnfcId) {
    var res_model = $.resource_model.resources ;
    var parts = vnfcId.replace(/\./g, ".resources.").split(".") ;
    for(var i=0; i < parts.length; ++i ) {
        res_model = res_model[parts[i]] ;
    }
    return res_model.scalingStepId
}

function mapVnfcIdForScaleIn (vnfcId) {

    var slotIds = [];
    var vnfcNames = [];
    var nfvIds = [];

    mgGroups = Object.keys($.resource_model.resources.mgRedundantAspectGroup.resources);
    lbGroups = Object.keys($.resource_model.resources.loadBalancerAspectGroup.resources);

    // fill vnfcNames, slotIds and nfvIds arrays with the VNFC IDs of all the already deployed MG/LB VMs

    for(var iter = 0 ; iter < mgGroups.length ; iter++ )
    {
        slotIds.push($.resource_model.resources.mgRedundantAspectGroup.resources[mgGroups[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId);
        slotIds.push($.resource_model.resources.mgRedundantAspectGroup.resources[mgGroups[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_slotId);
        vnfcNames.push($.resource_model.resources.mgRedundantAspectGroup.resources[mgGroups[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.attributes.name);
        vnfcNames.push($.resource_model.resources.mgRedundantAspectGroup.resources[mgGroups[iter]].resources.mgProtect.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.attributes.name);
        nfvIds.push("mgRedundantAspectGroup." + mgGroups[iter] + ".mgWorking.mgInstanceGroup.0.mgServerInstance");
        nfvIds.push("mgRedundantAspectGroup." + mgGroups[iter] + ".mgProtect.mgInstanceGroup.0.mgServerInstance");
    }

    for(var iter = 0 ; iter < lbGroups.length ; iter++ )
    {
        slotIds.push($.resource_model.resources.loadBalancerAspectGroup.resources[lbGroups[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.metadata.nokia_vnf_slotId);
        vnfcNames.push($.resource_model.resources.loadBalancerAspectGroup.resources[lbGroups[iter]].resources.loadBalancerInstanceGroup.resources["0"].resources.loadBalancerServerInstance.attributes.name);
        nfvIds.push("loadBalancerAspectGroup." + lbGroups[iter] + ".loadBalancerInstanceGroup.0.loadBalancerServerInstance");
    }
    try {
        // check if input is included in slotIds array
        if (slotIds.indexOf(vnfcId) >= 0 && vnfcId.length <= 2 && vnfcId.length > 0) {
            var slotIndex = slotIds.indexOf(vnfcId);
            return nfvIds[slotIndex]
        }
        // check if input is included in vnfcNames array
        else if (vnfcNames.indexOf(vnfcId) >= 0 && vnfcId.length > 2) {
            var NameIndex = vnfcNames.indexOf(vnfcId);
            return nfvIds[NameIndex]
        }
        // check if input is included in nfvIds array
        else if (nfvIds.indexOf(vnfcId) >= 0 && vnfcId.length > 2) {
            return vnfcId
        }
        else
        {
            throw new Error('Invalid VNFC Id was provided');
        }
    }
    catch (error) {
        var customMessage = error.message + ": " + vnfcId;
        return customMessage;
    }
}

if( $.operation_params.type.toLowerCase() == "in" &&  $.operation_params.additionalParams.vnfcToScaleIn.toLowerCase() != "detect" ) {
    scalingStepsToRemove = [] ;
    var vnfcsToRemove = $.operation_params.additionalParams.vnfcToScaleIn ;
    var vnfcToRemoveArray = vnfcsToRemove.split(',') ;
    for (var iter = 0 ; iter < $.operation_params.numberOfSteps; iter ++) {
        var vnfcIdToRemove = vnfcToRemoveArray[iter] ;
        var mappedNfvId = mapVnfcIdForScaleIn(vnfcIdToRemove);
        scalingStepsToRemove.push(findScalingStepIdByVnfcId(mappedNfvId)) ;
    }
    aux_params.scaling_decrements = {} ;
    aux_params.scaling_decrements[$.operation_params.aspectId] = scalingStepsToRemove ;
}

return aux_params
