package app.pooi.common.multitenancy;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

public class ApplicationInfoHolder {

    private final TransmittableThreadLocal<ApplicationInfo> applicationInfo =
            new TransmittableThreadLocal<>();

    public String getApplicationCode() {
        return Optional.ofNullable(applicationInfo.get())
                .map(ApplicationInfo::getApplicationCode).orElse("");
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo.set(applicationInfo);
    }

    public void clearApplicationInfo() {
        this.applicationInfo.remove();
    }
}
