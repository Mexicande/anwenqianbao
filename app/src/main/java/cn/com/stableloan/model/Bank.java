package cn.com.stableloan.model;

/**
 * Created by apple on 2017/7/4.
 */

public class Bank {

    /**
     * isSuccess : 1
     * bank : {"debit":{"dnumber":"0","dname":"0","dperiod":"0","dbank":"0","dphone":"0"},"credit":{"cnumber":"0","cname":"0","cperiod":"0","cbank":"0","cphone":"0"},"blass_time":"0"}
     * status : 1
     */

    private String isSuccess;
    private BankBean bank;
    private String status;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public BankBean getBank() {
        return bank;
    }

    public void setBank(BankBean bank) {
        this.bank = bank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class BankBean {
        /**
         * debit : {"dnumber":"0","dname":"0","dperiod":"0","dbank":"0","dphone":"0"}
         * credit : {"cnumber":"0","cname":"0","cperiod":"0","cbank":"0","cphone":"0"}
         * blass_time : 0
         */
        private String bstatus;

        public String getBstatus() {
            return bstatus;
        }

        public void setBstatus(String bstatus) {
            this.bstatus = bstatus;
        }

        private DebitBean debit;
        private CreditBean credit;
        private String blass_time;

        public DebitBean getDebit() {
            return debit;
        }

        public void setDebit(DebitBean debit) {
            this.debit = debit;
        }

        public CreditBean getCredit() {
            return credit;
        }

        public void setCredit(CreditBean credit) {
            this.credit = credit;
        }

        public String getBlass_time() {
            return blass_time;
        }

        public void setBlass_time(String blass_time) {
            this.blass_time = blass_time;
        }

        public static class DebitBean {

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof DebitBean)) return false;

                DebitBean debitBean = (DebitBean) o;

                if (!getDnumber().equals(debitBean.getDnumber())) return false;
                if (!getDname().equals(debitBean.getDname())) return false;
                if (!getDperiod().equals(debitBean.getDperiod())) return false;
                if (!getDbank().equals(debitBean.getDbank())) return false;
                return getDphone().equals(debitBean.getDphone());

            }

            @Override
            public int hashCode() {
                int result = getDnumber().hashCode();
                result = 31 * result + getDname().hashCode();
                result = 31 * result + getDperiod().hashCode();
                result = 31 * result + getDbank().hashCode();
                result = 31 * result + getDphone().hashCode();
                return result;
            }

            /**
             * dnumber : 0
             * dname : 0
             * dperiod : 0
             * dbank : 0
             * dphone : 0
             */

            private String dnumber;
            private String dname;
            private String dperiod;
            private String dbank;
            private String dphone;

            public String getDnumber() {
                return dnumber;
            }

            public void setDnumber(String dnumber) {
                this.dnumber = dnumber;
            }

            public String getDname() {
                return dname;
            }

            public void setDname(String dname) {
                this.dname = dname;
            }

            public String getDperiod() {
                return dperiod;
            }

            public void setDperiod(String dperiod) {
                this.dperiod = dperiod;
            }

            public String getDbank() {
                return dbank;
            }

            public void setDbank(String dbank) {
                this.dbank = dbank;
            }

            public String getDphone() {
                return dphone;
            }

            public void setDphone(String dphone) {
                this.dphone = dphone;
            }
        }

        public static class CreditBean {

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof CreditBean)) return false;

                CreditBean that = (CreditBean) o;

                if (!getCnumber().equals(that.getCnumber())) return false;
                if (!getCname().equals(that.getCname())) return false;
                if (!getCperiod().equals(that.getCperiod())) return false;
                if (!getCbank().equals(that.getCbank())) return false;
                return getCphone().equals(that.getCphone());

            }

            @Override
            public int hashCode() {
                int result = getCnumber().hashCode();
                result = 31 * result + getCname().hashCode();
                result = 31 * result + getCperiod().hashCode();
                result = 31 * result + getCbank().hashCode();
                result = 31 * result + getCphone().hashCode();
                return result;
            }

            /**
             * cnumber : 0
             * cname : 0
             * cperiod : 0
             * cbank : 0
             * cphone : 0
             */

            private String cnumber;
            private String cname;
            private String cperiod;
            private String cbank;
            private String cphone;

            public String getCnumber() {
                return cnumber;
            }

            public void setCnumber(String cnumber) {
                this.cnumber = cnumber;
            }

            public String getCname() {
                return cname;
            }

            public void setCname(String cname) {
                this.cname = cname;
            }

            public String getCperiod() {
                return cperiod;
            }

            public void setCperiod(String cperiod) {
                this.cperiod = cperiod;
            }

            public String getCbank() {
                return cbank;
            }

            public void setCbank(String cbank) {
                this.cbank = cbank;
            }

            public String getCphone() {
                return cphone;
            }

            public void setCphone(String cphone) {
                this.cphone = cphone;
            }
        }
    }
}