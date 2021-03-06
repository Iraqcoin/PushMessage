/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.handlers;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.stats.Profiler;
import com.vng.zing.stats.ThreadProfiler;
import com.vng.zing.pusheventmessage.model.TrackModel;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * @Note: Class handler xử lý http request, class handler được thiết kế để cho
 * phép tạo rất nhiều object, mỗi object tương đương với 1 request đầu vào
 * (object tạm thời), phần xử lý (hàm doProcess) chỉ đơn giản gọi lại phần xử lý
 * trong Model tương ứng
 *
 * => Không nên tạo thuộc tính thành viên trong class handler (vì sẽ có rất
 * nhiều object handler được tạo ra), hoặc chỉ tạo các thuộc tính primitive
 * types (boolean, char, short, int, long, float, double), hoặc chỉ tạo thuộc
 * tính static. Nếu cần thì hãy thêm trong class model tương ứng.
 *
 * @author namnq
 */
public class TrackHandler extends HttpServlet {

	private static final Logger _Logger = ZLogger.getLogger(TrackHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doProcess(req, resp);
	}

	private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
		ThreadProfiler profiler = Profiler.createThreadProfilerInHttpProc("TrackHandler", req);
		try {
			TrackModel.Instance.process(req, resp);
		} catch (Exception ex) {
			_Logger.error(null, ex);
		} finally {
			Profiler.closeThreadProfiler();
		}
	}
}
