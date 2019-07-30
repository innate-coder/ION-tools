package models;

import java.util.ArrayList;

public class CMgCbamGrantless implements MainModelClass {

    private String flavourId;
    private String instantiationLevelId;
    private boolean grantlessMode;
    private ArrayList<Vims> vims;
    private ArrayList<ExtVirtualLinks> extVirtualLinks;
    private ArrayList<ExtManagedVirtualLinks> extManagedVirtualLinks;
    private ArrayList<ComputeResourceFlavours> computeResourceFlavours;
    private ArrayList<SoftwareImages> softwareImages;
    private ArrayList<Zones> zones;

    public ArrayList<ExtVirtualLinks> getExtVirtualLinks() {
        return extVirtualLinks;
    }

    public void setExtVirtualLinks(ArrayList<ExtVirtualLinks> extVirtualLinks) {
        this.extVirtualLinks = extVirtualLinks;
    }

    public boolean isGrantlessMode() {
        return grantlessMode;
    }

    public void setGrantlessMode(boolean grantlessMode) {
        this.grantlessMode = grantlessMode;
    }

    public ArrayList<Zones> getZones() {
        return zones;
    }

    public void setZones(ArrayList<Zones> zones) {
        this.zones = zones;
    }

    public ArrayList<SoftwareImages> getSoftwareImages() {
        return softwareImages;
    }

    public void setSoftwareImages(ArrayList<SoftwareImages> softwareImages) {
        this.softwareImages = softwareImages;
    }

    public ArrayList<ComputeResourceFlavours> getComputeResourceFlavours() {
        return computeResourceFlavours;
    }

    public void setComputeResourceFlavours(ArrayList<ComputeResourceFlavours> computeResourceFlavours) {
        this.computeResourceFlavours = computeResourceFlavours;
    }

    public ArrayList<ExtManagedVirtualLinks> getExtManagedVirtualLinks() {
        return extManagedVirtualLinks;
    }

    public void setExtManagedVirtualLinks(ArrayList<ExtManagedVirtualLinks> extManagedVirtualLinks) {
        this.extManagedVirtualLinks = extManagedVirtualLinks;
    }

    public String getInstantiationLevelId() {
        return instantiationLevelId;
    }

    public void setInstantiationLevelId(String instantiationLevelId) {
        this.instantiationLevelId = instantiationLevelId;
    }

    public String getFlavourId() {
        return flavourId;
    }

    public void setFlavourId(String flavourId) {
        this.flavourId = flavourId;
    }

    public ArrayList<Vims> getVims() {
        return vims;
    }

    public void setVims(ArrayList<Vims> vims) {
        this.vims = vims;
    }

    public static class Zones {
        private String id;
        private String resourceId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }
    }

    public static class SoftwareImages {
        private String vnfdSoftwareImageId;
        private String resourceId;

        public String getVnfdSoftwareImageId() {
            return vnfdSoftwareImageId;
        }

        public void setVnfdSoftwareImageId(String vnfdSoftwareImageId) {
            this.vnfdSoftwareImageId = vnfdSoftwareImageId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }
    }

    public static class ExtVirtualLinks {

        private String resourceId;
        private ArrayList<ExtCps> extCps;

        public ArrayList<ExtCps> getExtCps() {
            return extCps;
        }

        public void setExtCps(ArrayList<ExtCps> extCps) {
            this.extCps = extCps;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }


        public static class ExtCps {
            private String cpdId;
            private ArrayList<Addresses> addresses;

            public ArrayList<Addresses> getAddresses() {
                return addresses;
            }

            public void setAddresses(ArrayList<Addresses> addresses) {
                this.addresses = addresses;
            }

            public String getCpdId() {
                return cpdId;
            }

            public void setCpdId(String cpdId) {
                this.cpdId = cpdId;
            }

            public static class Addresses {
                private String subnetId;
                private String ip;

                public String getSubnetId() {
                    return subnetId;
                }

                public void setSubnetId(String subnetId) {
                    this.subnetId = subnetId;
                }

                public String getIp() {
                    return ip;
                }

                public void setIp(String ip) {
                    this.ip = ip;
                }
            }

        }
    }

    public static class ExtManagedVirtualLinks {

        private String virtualLinkDescId;
        private String resourceId;

        public String getVirtualLinkDescId() {
            return virtualLinkDescId;
        }

        public void setVirtualLinkDescId(String virtualLinkDescId) {
            this.virtualLinkDescId = virtualLinkDescId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }
    }

    public static class ComputeResourceFlavours {

        private String vnfdVirtualComputeDescId;
        private String resourceId;

        public String getVnfdVirtualComputeDescId() {
            return vnfdVirtualComputeDescId;
        }

        public void setVnfdVirtualComputeDescId(String vnfdVirtualComputeDescId) {
            this.vnfdVirtualComputeDescId = vnfdVirtualComputeDescId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }
    }

    public static class Vims {

        private String id;
        private String interfaceEndpoint;

        private InterfaceInfo interfaceInfo;

        private AccessInfo accessInfo;


        public String getInterfaceEndpoint() {
            return interfaceEndpoint;
        }

        public void setInterfaceEndpoint(String interfaceEndpoint) {
            this.interfaceEndpoint = interfaceEndpoint;
        }

        public AccessInfo getAccessInfo() {
            return accessInfo;
        }

        public void setAccessInfo(AccessInfo accessInfo) {
            this.accessInfo = accessInfo;
        }

        public InterfaceInfo getInterfaceInfo() {
            return interfaceInfo;
        }

        public void setInterfaceInfo(InterfaceInfo interfaceInfo) {
            this.interfaceInfo = interfaceInfo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class InterfaceInfo {
            String region;

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }
        }

        public static class AccessInfo {
            private String password;
            private String username;
            private String tenant;

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getTenant() {
                return tenant;
            }

            public void setTenant(String tenant) {
                this.tenant = tenant;
            }
        }
    }


}
