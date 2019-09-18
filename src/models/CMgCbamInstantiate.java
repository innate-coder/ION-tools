package models;

import java.util.ArrayList;

public class CMgCbamInstantiate implements MainModelClass {
	
	private String apiVersion;
	private ArrayList<VimConnectionInfo> vimConnectionInfo;
	private String flavourId;
	private String instantiationLevelId;
	private ArrayList<ExtManagedVirtualLinks> extManagedVirtualLinks;
	private ArrayList<ExtVirtualLinks> extVirtualLinks;	
	private boolean grantlessMode;
	private ArrayList<ComputeResourceFlavours> computeResourceFlavours;
	private ArrayList<SoftwareImages> softwareImages;
	private ArrayList<Zone> zones;		
		
	public String getAPiVersion(){
		return this.apiVersion;
	}

	public void setApiVersion(String apiVersion){
		this.apiVersion = apiVersion;
	}
	
	public void setVimConnectionInfo(ArrayList<VimConnectionInfo> vimConnectionInfo){
	   this.vimConnectionInfo = vimConnectionInfo;	
	}
	
	public ArrayList<VimConnectionInfo> getVimConnectionInfo(){
		return this.vimConnectionInfo;
	}
	
	public String getflavourId(){
		return this.flavourId;
	}

	public void setFlavourId(String flavourId){
		this.flavourId = flavourId;
	}
	
	public String getInstantiationLevelId(){
		return this.instantiationLevelId;
	}

	public void setInstantiationLevelId(String instantiationLevelId){
		this.instantiationLevelId = instantiationLevelId;
	}	
	
	public void setExtManagedVirtualLinks(ArrayList<ExtManagedVirtualLinks> extManagedVirtualLinks){
		this.extManagedVirtualLinks = extManagedVirtualLinks;
	}
	
	public ArrayList<ExtManagedVirtualLinks> getExtManagedVirtualLinks(){
		return this.extManagedVirtualLinks;
	}
	
	public void setExtVirtualLinks(ArrayList<ExtVirtualLinks> extVirtualLinks){
		this.extVirtualLinks = extVirtualLinks;
	}
	
	public ArrayList<ExtVirtualLinks> getExtVirtualLinks(){
		return this.extVirtualLinks;
	}
	
	public boolean getGrantlessMode(){
		return grantlessMode;
	}
	
	public void setGrantlessMode(boolean grantlessMode){
		this.grantlessMode= grantlessMode;
	}
	
	public void setComputeResourceFlavours(ArrayList<ComputeResourceFlavours> computeResourceFlavours){
		this.computeResourceFlavours = computeResourceFlavours;
	}
	
	public ArrayList<ComputeResourceFlavours> getComputeResourceFlavours(){
		return this.computeResourceFlavours;
	}	
	
	public void setSoftwareImages(ArrayList<SoftwareImages> softwareImages){
		this.softwareImages = softwareImages;
	}
	
	public ArrayList<SoftwareImages> getSoftwareImages(){
		return this.softwareImages;
	}
	
	public void setZone(ArrayList<Zone> zones){
		this.zones = zones;
	}
	
	public ArrayList<Zone> getZones(){
		return this.zones;
	}
	
	public static class VimConnectionInfo{
		private AccessInfo accessInfo;
		private String id;
		private InterfaceInfo interfaceInfo;	
		private String vimType;
		
		public AccessInfo getAccessInfo() {
	         return accessInfo;
	    }
	    public void setAccessInfo(AccessInfo accessInfo) {
	         this.accessInfo = accessInfo;
	    }
		
		public String getId(){
			return this.id;
		}

		public void setId(String id){
			this.id = id;
		}	
		
		public InterfaceInfo getInterfaceInfo() {
	         return interfaceInfo;
	    }
	    public void setInterfaceInfo(InterfaceInfo interfaceInfo) {
	         this.interfaceInfo = interfaceInfo;
	    }
	    
	    public String getVimType(){
			return this.vimType;
		}

		public void setVimType(String vimType){
			this.vimType = vimType;
		}
		
	    public static class InterfaceInfo{
	    	private String endpoint;
	    	
	    	public String getEndpoint(){
				return this.endpoint;
			}

			public void setEndpoint(String endpoint){
				this.endpoint = endpoint;
			}		
	    }
	    
	    public static class AccessInfo {
	        private String username;
	        private String password;
	        private String region;
	        private String tenant;          
	        private String project;
	        private String userDomain;
	        private String projectDomain;

	        public String getUsername() {
	            return username;
	        }

	        public void setUsername(String username) {
	            this.username = username;
	        }
	          
	        public String getPassword() {
	            return password;
	        }

	        public void setPassword(String password) {
	            this.password = password;
	        }
	         
	        public String getRegion() {
	            return region;
	        }

	        public void setRegion(String region) {
	            this.region = region;
	        }
	          
	        public String getTenant() {
	            return tenant;
	        }

	        public void setTenant(String tenant) {
	            this.tenant = tenant;
	        }
	          
	        public String getProject() {
	            return project;
	        }

	        public void setProject(String project) {
	            this.project = project;
	        }
	          
	        public String getUserDomain() {
	            return userDomain;
	        }

	        public void setUserDomain(String userDomain) {
	            this.userDomain = userDomain;
	        }
	          
	        public String getProjectDomain(){
	            return this.projectDomain;
	        }

	        public void setProjectDomain(String projectDomain) {
	            this.projectDomain = projectDomain;
	        }
	      }
	}
	
	public static class ExtManagedVirtualLinks{
		private String id;
		private String vimConnectionId;
		private String resourceId;
		private String virtualLinkDescId;
		
		public void setId(String id){
			this.id = id;
		}
		
		public String getId(){
			return this.id;
		}
		
		public void setVimConnectionId(String vimConnectionId){
			this.vimConnectionId = vimConnectionId;
		}
		
		public String getVimConnectionId(){
			return this.vimConnectionId;
		}
		
		public void setResourceId(String resourceId){
			this.resourceId = resourceId;
		}
		
		public String getResourceId(){
			return this.resourceId;
		}
		
		public void setVirtualLinkDescId(String virtualLinkDescId){
			this.virtualLinkDescId = virtualLinkDescId;
		}
		
		public String getVirtualLinkDescId(){
			return this.virtualLinkDescId;
		}
	}
	
	public static class ExtVirtualLinks{
		
		private ArrayList<ExtCps> extCps;
		private String id;
		private String vimConnectionId;
		private String resourceId;		
		
		public String getId(){
			return this.id;
		}
		
		public void setId(String id){
			this.id=id;
		}
		
		public String getVimConnectionId(){
			return this.vimConnectionId;
		}
		
		public void setVimConnectionId(String vimConnectionId){
			this.vimConnectionId=vimConnectionId;
		}
		
		public String getResourceId(){
			return this.resourceId;
		}
		
		public void setResourceId(String resourceId){
			this.resourceId=resourceId;
		}
		
		public void setExtCps(ArrayList<ExtCps> extCps){
			this.extCps = extCps;
		}
		
		public ArrayList<ExtCps> getExtCps(){
			return this.extCps;
		}
		
		public static class ExtCps{
			
			private ArrayList<CpConfig> cpConfig;
			private String cpdId;
			
			public String getCpdId(){
				return this.cpdId;
			}
			
			public void setCpdId(String cpdId){
				this.cpdId = cpdId;
			}
			
			public ArrayList<CpConfig> getCpConfig(){
				return this.cpConfig;
			}
			
			public void setCpConfig(ArrayList<CpConfig> cpConfig){
				this.cpConfig = cpConfig;
			}
			
			public static class CpConfig{
				
				private ArrayList<CpProtocolData> cpProtocolData;
				
				public void setCpProtocolData(ArrayList<CpProtocolData> cpProtocolData){
					this.cpProtocolData = cpProtocolData;
				}
				
				public ArrayList<CpProtocolData> getCpProtocolData(){
					return this.cpProtocolData;
				}
				
				public static class CpProtocolData{
					
					private String layerProtocol;
					private IpOverEthernet ipOverEthernet;
					
					public String getLayerProtocol(){
						return this.layerProtocol;
					}
					
					public void setLayerProtocol(String layerProtocol){
						this.layerProtocol = layerProtocol;
					}
					
					public IpOverEthernet getIpOverEthernet(){
						return this.ipOverEthernet;
					}
					
					public void setIpOverEthernet(IpOverEthernet ipOverEthernet){
						this.ipOverEthernet = ipOverEthernet;
					}
					
					public static class IpOverEthernet{
						
						private ArrayList<IpAddresses> ipAddresses;
						
						public void setIpAddresses(ArrayList<IpAddresses> ipAddresses){
							this.ipAddresses = ipAddresses;
						}
						
						public ArrayList<IpAddresses> getIpAddresses(){
							return this.ipAddresses;
						}
						
						public static class IpAddresses{
							private String type;
							private ArrayList<String> fixedAddresses;
							private String subnetId;
							
							public String getType(){
								return this.type;
							}
							
							public void setType(String type){
								this.type = type;
							}
							
							public void setFixedAddresses(ArrayList<String> fixedAddresses){
								this.fixedAddresses = fixedAddresses;
							}
							
							public ArrayList<String> getFixedAddresses(){
								return this.fixedAddresses;
							}
							
							public String getSubnetId(){
								return this.subnetId;
							}
							
							public void setSubnetId(String subnetId){
								this.subnetId = subnetId;
							}
						}
					}
				}
			}
		}
		
	}
	
	public static class ComputeResourceFlavours{
		private String vimConnectionId;
		private String vimFlavourId;
		private String vnfdVirtualComputeDescId;
		
		public String getVimConnectionId(){
			return this.vimConnectionId;
		}
		
		public void setVimConnectionId(String vimConnectionId){
			this.vimConnectionId = vimConnectionId;
		}
		
		public String getVimFlavourId(){
			return this.vimFlavourId;
		}
		
		public void setVimFlavourId(String vimFlavourId){
			this.vimFlavourId = vimFlavourId;
		}
		
		public String getVnfdVirtualComputeDescId(){
			return this.vnfdVirtualComputeDescId;
		}
		
		public void setVnfdVirtualComputeDescId(String vnfdVirtualComputeDescId){
			this.vnfdVirtualComputeDescId = vnfdVirtualComputeDescId;
		}
	}
	
	public static class SoftwareImages{
		private String vimConnectionId;
		private String vnfdSoftwareImageId;
		private String vimSoftwareImageId;
		
		public String getVimConnectionId(){
			return this.vimConnectionId;
		}
		
		public void setVimConnectionId(String vimConnectionId){
			this.vimConnectionId = vimConnectionId;
		}
		
		public String getVnfdSoftwareImageId(){
			return this.vnfdSoftwareImageId;
		}
		
		public void setVnfdSoftwareImageId(String vnfdSoftwareImageId){
			this.vnfdSoftwareImageId = vnfdSoftwareImageId;
		}
		
		public String getVimSoftwareImageId(){
			return this.vimSoftwareImageId;
		}
		
		public void setVimSoftwareImageId(String vimSoftwareImageId){
			this.vimSoftwareImageId = vimSoftwareImageId;
		}
	}
	
	public static class Zone{
		private String id;
		private String zoneId;
		private String vimConnectionId;
		
		public String getId(){
			return this.id;
		}
		
		public void setId(String id){
			this.id = id;
		}
		
		public String getZoneId(){
			return this.zoneId;
		}
		
		public void setZoneId(String zoneId){
			this.zoneId = zoneId;
		}
		
		public String getVimConnectionId(){
			return this.vimConnectionId;
		}
		
		public void setVimConnectionId(String vimConnectionId){
			this.vimConnectionId = vimConnectionId;
		}
	}
	
}
		
	
	

