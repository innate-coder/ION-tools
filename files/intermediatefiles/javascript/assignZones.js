$.stack_params.cbam.resources.oamAspectGroup["0"].OAMActive.oamInstanceGroup["0"].oamServerInstance.azId = $.nfv_model.zones.workingZones.zoneId ; // active
$.stack_params.cbam.resources.oamAspectGroup["0"].OAMStandby.oamInstanceGroup["0"].oamServerInstance.azId = $.nfv_model.zones.protectZones.zoneId ; // standby

var lbRgIndexes = getLbRgIndexes();
var mgRgIndexes = getMgRgIndexes();

for (var iter = 0; iter < mgRgIndexes.length; iter++)
{
    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].mgWorking.mgInstanceGroup["0"].mgServerInstance.azId = $.nfv_model.zones.workingZones.zoneId ; // active
    $.stack_params.cbam.resources.mgRedundantAspectGroup[mgRgIndexes[iter]].mgProtect.mgInstanceGroup["0"].mgServerInstance.azId = $.nfv_model.zones.protectZones.zoneId ; // standby
}

for (var iter = 0; iter < lbRgIndexes.length; iter++)
{
    var lbMappedIndex = $.stack_params.cbam.resources.loadBalancerAspectGroup[lbRgIndexes[iter]]._mappedIndex;

    if (lbMappedIndex%2 == 0 )
    {
       $.stack_params.cbam.resources.loadBalancerAspectGroup[lbRgIndexes[iter]].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.azId = $.nfv_model.zones.workingZones.zoneId ;

    } else {

       $.stack_params.cbam.resources.loadBalancerAspectGroup[lbRgIndexes[iter]].loadBalancerInstanceGroup["0"].loadBalancerServerInstance.azId = $.nfv_model.zones.protectZones.zoneId ;
    }

}

return $.stack_params

function isNumeric(n) {
  return !isNaN(parseFloat(n)) && isFinite(n)
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
