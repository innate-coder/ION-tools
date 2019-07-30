var mgRgIndexes = getMgRgIndexes();

for(var iter =  0 ; iter < mgRgIndexes.length; iter++ )
{
    if (!operationIsScaling())
    {
        if ($.resource_model.resources == undefined )
        {
            $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.stack_params.cbam.extensions.controlPlaneCores ;
            $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.stack_params.cbam.extensions.compactFp ;

        } else
        {
            if (!operationIsUpgrade()) {

                if($.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cpCores == undefined )
                {
                    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.stack_params.cbam.extensions.controlPlaneCores ;
                } else {
                    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cpCores ;
                }
                if($.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cfp == undefined )
                {
                    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.stack_params.cbam.extensions.compactFp ;
                } else {
                    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cfp ;
                }

            }
            else {

                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.stack_params.cbam.extensions.controlPlaneCores ;

                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.stack_params.cbam.extensions.compactFp ;
            }
        }
    } else
    {

        if ( $.operation_params.type.toLowerCase() == "out" && iter >= ( mgRgIndexes.length - numberofScalingStepsMG()) )
        {
            var inputParamCpCores = $.operation_params.additionalParams.cpCoreAllocation ;
            var inputParamUpCfp = $.operation_params.additionalParams.upCompactFP ;
            if (inputParamCpCores.toString() == 'default')
            {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.stack_params.cbam.extensions.controlPlaneCores;
            } else
            {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = inputParamCpCores.toString() ;
            }
            if (inputParamUpCfp.toString() == 'default')
            {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.stack_params.cbam.extensions.compactFp ;
            } else
            {
                $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = inputParamUpCfp.toString() ;
            }
        } else
        {
            $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cpCores = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cpCores ;
            $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].cfp = $.resource_model.resources.mgRedundantAspectGroup.resources[mgRgIndexes[iter]].resources.mgWorking.resources.mgInstanceGroup.resources["0"].resources.mgServerInstance.metadata.nokia_vnf_cfp ;
        }
    }
}
return $.stack_params

function isNumeric(n) {
  return !isNaN(parseFloat(n)) && isFinite(n)
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

function numberofScalingStepsMG()  {

    if ($.operation_params.aspectId == "mgRedundantAspect") {
        var numberofSteps = parseInt($.operation_params.numberOfSteps);
        return numberofSteps;
    }

    return 0;
}
