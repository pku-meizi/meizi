package com.meiziaccess.uploadModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user-u1 on 2016/5/27.
 */

public interface UploadLogRepository extends JpaRepository<UploadLog, Long> {
}
