package com.blink.ewb.fastdfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.blink.ewb.Config;
import com.blink.ewb.EwbFileServer;

@Service
public class FastdfsClient {

	private static Logger logger = LoggerFactory.getLogger(EwbFileServer.class);

	/**
	 * 
	 * 
	 */
	public static void init() {
		try {
			ClientGlobal.init("./fdfs_client.conf");
		} catch (FileNotFoundException e) {
			logger.error("Exception occured : ", e);
		} catch (IOException e) {
			logger.error("Exception occured : ", e);
		} catch (MyException e) {
			logger.error("Exception occured : ", e);
		}
	}

	/**
	 * 
	 * 
	 * @param filePath
	 * @param fileExtName
	 * @return
	 * @throws Exception
	 */
	public static FastdfsVo uploadFile(String filePath, String fileExtName) throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		byte[] fileData = FileUtils.readFileToByteArray(file);
		return uploadFile(fileData, fileExtName);
	}

	/**
	 * 
	 * 
	 * @param fileData
	 * @param fileExt
	 * @return
	 * @throws Exception
	 */
	public static FastdfsVo uploadFile(byte fileData[], String fileExt) throws Exception {
		TrackerServer trackerServer = null;
		StorageServer storageServer = null;
		try {
			if (fileData == null) {
				return null;
			}
			StorageClient client = getStorageClient(storageServer, trackerServer);
			String[] results = client.upload_file(fileData, fileExt, null);
			if (results == null || results.length < 2) {
				return null;
			}
			String fileUrl = Config.fastdfsUrl;
			if (ClientGlobal.g_tracker_http_port != 80) {
				fileUrl += ":" + ClientGlobal.g_tracker_http_port;
			}
			String fileGroup = results[0];
			String fileName = results[1];
			String fileId = fileGroup + StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR + fileName;
			fileUrl += "/" + fileId;
			if (ClientGlobal.g_anti_steal_token) {
				int ts = (int) (System.currentTimeMillis() / 1000);
				String token = ProtoCommon.getToken(fileId, ts, ClientGlobal.g_secret_key);
				fileUrl += "?token=" + token + "&ts=" + ts;
			}

			FastdfsVo fastdfsBean = new FastdfsVo();
			fastdfsBean.setFileLength(fileData.length);
			fastdfsBean.setRemoteFileGroup(fileGroup);
			fastdfsBean.setRemoteFileName(fileName);
			fastdfsBean.setRemoteFileExt(fileExt);
			fastdfsBean.setRemoteFileUrl(fileUrl);
			return fastdfsBean;
		} finally {
			close(storageServer, trackerServer);
		}
	}

	/**
	 * 
	 * 
	 * @param groupName
	 * @param remoteFileName
	 * @return
	 * @throws Exception
	 */
	public static byte[] downloadFile(String groupName, String remoteFileName) throws Exception {
		StorageServer storageServer = null;
		TrackerServer trackerServer = null;
		try {
			StorageClient client = getStorageClient(storageServer, trackerServer);
			return client.download_file(groupName, remoteFileName);
		} finally {
			close(storageServer, trackerServer);
		}
	}

	/**
	 * 
	 * 
	 * @param groupName
	 * @param remoteFileName
	 * @return
	 * @throws Exception
	 */
	public static int deleteFile(String groupName, String remoteFileName) throws Exception {
		int status = -1;
		StorageServer storageServer = null;
		TrackerServer trackerServer = null;
		try {
			StorageClient client = getStorageClient(storageServer, trackerServer);
			status = client.delete_file(groupName, remoteFileName);
		} finally {
			close(storageServer, trackerServer);
		}
		return status;
	}

	/**
	 * 
	 * 
	 * @param storageServer
	 * @param trackerServer
	 * @return
	 * @throws Exception
	 */
	private static StorageClient getStorageClient(StorageServer storageServer, TrackerServer trackerServer)
			throws Exception {
		trackerServer = new TrackerClient().getConnection();
		return new StorageClient(trackerServer, storageServer);
	}

	/**
	 * 
	 * 
	 * @param storageServer
	 * @param trackerServer
	 */
	private static void close(StorageServer storageServer, TrackerServer trackerServer) {
		if (storageServer != null) {
			try {
				storageServer.close();
				storageServer = null;
			} catch (Exception e) {
				logger.error("Exception occured : ", e);
			}
		}
		if (trackerServer != null) {
			try {
				trackerServer.close();
				trackerServer = null;
			} catch (Exception e) {
				logger.error("Exception occured : ", e);
			}
		}
	}

}
