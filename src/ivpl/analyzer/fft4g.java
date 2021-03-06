package ivpl.analyzer;

/* publicの関数をpackage privateに変更
 * メンバ変数をprivate finalに変更
 * 元ソース：
 * https://qiita.com/YSRKEN/items/600091a1e8c02bdf5823
 *
 *
 * fft4g.cのJava移植版
 * 元ソース：
 * 京都大学助教授の大浦拓哉氏がフリーソフトとして提供する
 * 「汎用 FFT (高速 フーリエ/コサイン/サイン 変換) パッケージ」
 * (http://www.kurims.kyoto-u.ac.jp/~ooura/fft-j.html)
 * のfft4g.c
 * 概要：
 * データ数N(2の冪乗)の1次元データに対して、離散フーリエ・コサイン・サイン変換を行う。
 * 内部でテーブルを利用するタイプで、インプレース型なので破壊的関数である。
 * 関数の使い方：
 * void cdft(const int isgn, double *a);	//複素離散フーリエ変換
 * void rdft(const int isgn, double *a);	//実数離散フーリエ変換
 * void ddct(const int isgn, double *a);	//離散コサイン変換
 * void ddst(const int isgn, double *a);	//離散サイン変換
 * void dfct(double *a, double *t); //実対称フーリエ変換を用いたコサイン変換
 * void dfst(double *a, double *t); //実非対称フーリエ変換を用いたサイン変換
 */

final class fft4g{
	// メンバ変数
	private final int[] ip;   //ビット反転に使用する作業領域
	private final double[] w; //cosとsinのテーブル(ip[0] == 0だと初期化される)
	private final int n;	  //配列の要素数(2N)
	// コンストラクタ
	fft4g(int n) {
		this.n = n;
		ip = new int[2 + (int)Math.sqrt(0.5 * n) + 1];
		w = new double[n * 5 / 4];
		ip[0] = 0;
	}
	// publicメソッド
	//複素離散フーリエ変換
	void cdft(int isgn, double[] a){
		if (n > (ip[0] << 2)) {
			makewt(n >> 2, ip, w);
		}
		if (n > 4) {
			if (isgn >= 0) {
				bitrv2(n, ip, a);
				cftfsub(n, a, w);
			} else {
				bitrv2conj(n, ip, a);
				cftbsub(n, a, w);
			}
		} else if (n == 4) {
			cftfsub(n, a, w);
		}
		return;
	}

	//実数離散フーリエ変換
	void rdft(int isgn, double[] a){
		int nw, nc;
		double xi;

		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw, ip, w);
		}
		nc = ip[1];
		if (n > (nc << 2)) {
			nc = n >> 2;
			makect(nc, ip, w, nw);
		}
		if (isgn >= 0) {
			if (n > 4) {
				bitrv2(n, ip, a);
				cftfsub(n, a, w);
				rftfsub(n, a, nc, w, nw);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
			xi = a[0] - a[1];
			a[0] += a[1];
			a[1] = xi;
		} else {
			a[1] = 0.5 * (a[0] - a[1]);
			a[0] -= a[1];
			if (n > 4) {
				rftbsub(n, a, nc, w, nw);
				bitrv2(n, ip, a);
				cftbsub(n, a, w);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
		}
	}

	//離散コサイン変換
	void ddct(int isgn, double[] a){
		int j, nw, nc;
		double xr;

		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw, ip, w);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, ip, w, nw);
		}
		if (isgn < 0) {
			xr = a[n - 1];
			for (j = n - 2; j >= 2; j -= 2) {
				a[j + 1] = a[j] - a[j - 1];
				a[j] += a[j - 1];
			}
			a[1] = a[0] - xr;
			a[0] += xr;
			if (n > 4) {
				rftbsub(n, a, nc, w, nw);
				bitrv2(n, ip, a);
				cftbsub(n, a, w);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
		}
		dctsub(n, a, nc, w, nw);
		if (isgn >= 0) {
			if (n > 4) {
				bitrv2(n, ip, a);
				cftfsub(n, a, w);
				rftfsub(n, a, nc, w, nw);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
			xr = a[0] - a[1];
			a[0] += a[1];
			for (j = 2; j < n; j += 2) {
				a[j - 1] = a[j] - a[j + 1];
				a[j] += a[j + 1];
			}
			a[n - 1] = xr;
		}
	}

	//離散サイン変換
	void ddst(int isgn, double[] a){
		int j, nw, nc;
		double xr;

		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw, ip, w);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, ip, w, nw);
		}
		if (isgn < 0) {
			xr = a[n - 1];
			for (j = n - 2; j >= 2; j -= 2) {
				a[j + 1] = -a[j] - a[j - 1];
				a[j] -= a[j - 1];
			}
			a[1] = a[0] + xr;
			a[0] -= xr;
			if (n > 4) {
				rftbsub(n, a, nc, w, nw);
				bitrv2(n, ip, a);
				cftbsub(n, a, w);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
		}
		dstsub(n, a, nc, w, nw);
		if (isgn >= 0) {
			if (n > 4) {
				bitrv2(n, ip, a);
				cftfsub(n, a, w);
				rftfsub(n, a, nc, w, nw);
			} else if (n == 4) {
				cftfsub(n, a, w);
			}
			xr = a[0] - a[1];
			a[0] += a[1];
			for (j = 2; j < n; j += 2) {
				a[j - 1] = -a[j] - a[j + 1];
				a[j] -= a[j + 1];
			}
			a[n - 1] = -xr;
		}
	}

	//実対称フーリエ変換を用いたコサイン変換
	void dfct(double[] a, double[] t){
		int j, k, l, m, mh, nw, nc;
		double xr, xi, yr, yi;

		nw = ip[0];
		if (n > (nw << 3)) {
			nw = n >> 3;
			makewt(nw, ip, w);
		}
		nc = ip[1];
		if (n > (nc << 1)) {
			nc = n >> 1;
			makect(nc, ip, w, nw);
		}
		m = n >> 1;
		yi = a[m];
		xi = a[0] + a[n];
		a[0] -= a[n];
		t[0] = xi - yi;
		t[m] = xi + yi;
		if (n > 2) {
			mh = m >> 1;
			for (j = 1; j < mh; j++) {
				k = m - j;
				xr = a[j] - a[n - j];
				xi = a[j] + a[n - j];
				yr = a[k] - a[n - k];
				yi = a[k] + a[n - k];
				a[j] = xr;
				a[k] = yr;
				t[j] = xi - yi;
				t[k] = xi + yi;
			}
			t[mh] = a[mh] + a[n - mh];
			a[mh] -= a[n - mh];
			dctsub(m, a, nc, w, nw);
			if (m > 4) {
				bitrv2(m, ip, a);
				cftfsub(m, a, w);
				rftfsub(m, a, nc, w, nw);
			} else if (m == 4) {
				cftfsub(m, a, w);
			}
			a[n - 1] = a[0] - a[1];
			a[1] = a[0] + a[1];
			for (j = m - 2; j >= 2; j -= 2) {
				a[2 * j + 1] = a[j] + a[j + 1];
				a[2 * j - 1] = a[j] - a[j + 1];
			}
			l = 2;
			m = mh;
			while (m >= 2) {
				dctsub(m, t, nc, w, nw);
				if (m > 4) {
					bitrv2(m, ip, t);
					cftfsub(m, t, w);
					rftfsub(m, t, nc, w, nw);
				} else if (m == 4) {
					cftfsub(m, t, w);
				}
				a[n - l] = t[0] - t[1];
				a[l] = t[0] + t[1];
				k = 0;
				for (j = 2; j < m; j += 2) {
					k += l << 2;
					a[k - l] = t[j] - t[j + 1];
					a[k + l] = t[j] + t[j + 1];
				}
				l <<= 1;
				mh = m >> 1;
				for (j = 0; j < mh; j++) {
					k = m - j;
					t[j] = t[m + k] - t[m + j];
					t[k] = t[m + k] + t[m + j];
				}
				t[mh] = t[m + mh];
				m = mh;
			}
			a[l] = t[0];
			a[n] = t[2] - t[1];
			a[0] = t[2] + t[1];
		} else {
			a[1] = a[0];
			a[2] = t[0];
			a[0] = t[1];
		}
	}

	//実非対称フーリエ変換を用いたサイン変換
	void dfst(double[] a, double[] t){
		int j, k, l, m, mh, nw, nc;
		double xr, xi, yr, yi;

		nw = ip[0];
		if (n > (nw << 3)) {
			nw = n >> 3;
			makewt(nw, ip, w);
		}
		nc = ip[1];
		if (n > (nc << 1)) {
			nc = n >> 1;
			makect(nc, ip, w, nw);
		}
		if (n > 2) {
			m = n >> 1;
			mh = m >> 1;
			for (j = 1; j < mh; j++) {
				k = m - j;
				xr = a[j] + a[n - j];
				xi = a[j] - a[n - j];
				yr = a[k] + a[n - k];
				yi = a[k] - a[n - k];
				a[j] = xr;
				a[k] = yr;
				t[j] = xi + yi;
				t[k] = xi - yi;
			}
			t[0] = a[mh] - a[n - mh];
			a[mh] += a[n - mh];
			a[0] = a[m];
			dstsub(m, a, nc, w, nw);
			if (m > 4) {
				bitrv2(m, ip, a);
				cftfsub(m, a, w);
				rftfsub(m, a, nc, w, nw);
			} else if (m == 4) {
				cftfsub(m, a, w);
			}
			a[n - 1] = a[1] - a[0];
			a[1] = a[0] + a[1];
			for (j = m - 2; j >= 2; j -= 2) {
				a[2 * j + 1] = a[j] - a[j + 1];
				a[2 * j - 1] = -a[j] - a[j + 1];
			}
			l = 2;
			m = mh;
			while (m >= 2) {
				dstsub(m, t, nc, w, nw);
				if (m > 4) {
					bitrv2(m, ip, t);
					cftfsub(m, t, w);
					rftfsub(m, t, nc, w, nw);
				} else if (m == 4) {
					cftfsub(m, t, w);
				}
				a[n - l] = t[1] - t[0];
				a[l] = t[0] + t[1];
				k = 0;
				for (j = 2; j < m; j += 2) {
					k += l << 2;
					a[k - l] = -t[j] - t[j + 1];
					a[k + l] = t[j] - t[j + 1];
				}
				l <<= 1;
				mh = m >> 1;
				for (j = 1; j < mh; j++) {
					k = m - j;
					t[j] = t[m + k] + t[m + j];
					t[k] = t[m + k] - t[m + j];
				}
				t[0] = t[m + mh];
				m = mh;
			}
			a[l] = t[0];
		}
		a[0] = 0;
	}

	// privateメソッド
	private void makewt(int nw, int[] ip, double[] w){
		int j, nwh;
		double delta, x, y;

		ip[0] = nw;
		ip[1] = 1;
		if (nw > 2) {
			nwh = nw >> 1;
			delta = Math.atan(1.0) / nwh;
			w[0] = 1;
			w[1] = 0;
			w[nwh] = Math.cos(delta * nwh);
			w[nwh + 1] = w[nwh];
			if (nwh > 2) {
				for (j = 2; j < nwh; j += 2) {
					x = Math.cos(delta * j);
					y = Math.sin(delta * j);
					w[j] = x;
					w[j + 1] = y;
					w[nw - j] = y;
					w[nw - j + 1] = x;
				}
				bitrv2(nw, ip, w);
			}
		}
	}

	private void makect(int nc, int[] ip, double[] c, int nw){
		int j, nch;
		double delta;

		ip[1] = nc;
		if (nc > 1) {
			nch = nc >> 1;
			delta = Math.atan(1.0) / nch;
			c[0 + nw] = Math.cos(delta * nch);
			c[nch + nw] = 0.5 * c[0 + nw];
			for (j = 1; j < nch; j++) {
				c[j + nw] = 0.5 * Math.cos(delta * j);
				c[nc - j + nw] = 0.5 * Math.sin(delta * j);
			}
		}
	}

	private void bitrv2(int n, int[] ip, double[] a){
		int j, j1, k, k1, l, m, m2;
		double xr, xi, yr, yi;

		ip[0 + 2] = 0;
		l = n;
		m = 1;
		while ((m << 3) < l) {
			l >>= 1;
			for (j = 0; j < m; j++) {
				ip[m + j + 2] = ip[j + 2] + l;
			}
			m <<= 1;
		}
		m2 = 2 * m;
		if ((m << 3) == l) {
			for (k = 0; k < m; k++) {
				for (j = 0; j < k; j++) {
					j1 = 2 * j + ip[k + 2];
					k1 = 2 * k + ip[j + 2];
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += 2 * m2;
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 -= m2;
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += 2 * m2;
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
				}
				j1 = 2 * k + m2 + ip[k + 2];
				k1 = j1 + m2;
				xr = a[j1];
				xi = a[j1 + 1];
				yr = a[k1];
				yi = a[k1 + 1];
				a[j1] = yr;
				a[j1 + 1] = yi;
				a[k1] = xr;
				a[k1 + 1] = xi;
			}
		} else {
			for (k = 1; k < m; k++) {
				for (j = 0; j < k; j++) {
					j1 = 2 * j + ip[k + 2];
					k1 = 2 * k + ip[j + 2];
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += m2;
					xr = a[j1];
					xi = a[j1 + 1];
					yr = a[k1];
					yi = a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
				}
			}
		}
	}

	private void bitrv2conj(int n, int[] ip, double[] a){
		int j, j1, k, k1, l, m, m2;
		double xr, xi, yr, yi;

		ip[0 + 2] = 0;
		l = n;
		m = 1;
		while ((m << 3) < l) {
			l >>= 1;
			for (j = 0; j < m; j++) {
				ip[m + j + 2] = ip[j + 2] + l;
			}
			m <<= 1;
		}
		m2 = 2 * m;
		if ((m << 3) == l) {
			for (k = 0; k < m; k++) {
				for (j = 0; j < k; j++) {
					j1 = 2 * j + ip[k + 2];
					k1 = 2 * k + ip[j + 2];
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += 2 * m2;
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 -= m2;
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += 2 * m2;
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
				}
				k1 = 2 * k + ip[k + 2];
				a[k1 + 1] = -a[k1 + 1];
				j1 = k1 + m2;
				k1 = j1 + m2;
				xr = a[j1];
				xi = -a[j1 + 1];
				yr = a[k1];
				yi = -a[k1 + 1];
				a[j1] = yr;
				a[j1 + 1] = yi;
				a[k1] = xr;
				a[k1 + 1] = xi;
				k1 += m2;
				a[k1 + 1] = -a[k1 + 1];
			}
		} else {
			a[1] = -a[1];
			a[m2 + 1] = -a[m2 + 1];
			for (k = 1; k < m; k++) {
				for (j = 0; j < k; j++) {
					j1 = 2 * j + ip[k + 2];
					k1 = 2 * k + ip[j + 2];
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
					j1 += m2;
					k1 += m2;
					xr = a[j1];
					xi = -a[j1 + 1];
					yr = a[k1];
					yi = -a[k1 + 1];
					a[j1] = yr;
					a[j1 + 1] = yi;
					a[k1] = xr;
					a[k1 + 1] = xi;
				}
				k1 = 2 * k + ip[k + 2];
				a[k1 + 1] = -a[k1 + 1];
				a[k1 + m2 + 1] = -a[k1 + m2 + 1];
			}
		}
	}

	private void cftfsub(int n, double[] a, double[] w){
		int j, j1, j2, j3, l;
		double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		l = 2;
		if (n > 8) {
			cft1st(n, a, w);
			l = 8;
			while ((l << 2) < n) {
				cftmdl(n, l, a, w);
				l <<= 2;
			}
		}
		if ((l << 2) == n) {
			for (j = 0; j < l; j += 2) {
				j1 = j + l;
				j2 = j1 + l;
				j3 = j2 + l;
				x0r = a[j] + a[j1];
				x0i = a[j + 1] + a[j1 + 1];
				x1r = a[j] - a[j1];
				x1i = a[j + 1] - a[j1 + 1];
				x2r = a[j2] + a[j3];
				x2i = a[j2 + 1] + a[j3 + 1];
				x3r = a[j2] - a[j3];
				x3i = a[j2 + 1] - a[j3 + 1];
				a[j] = x0r + x2r;
				a[j + 1] = x0i + x2i;
				a[j2] = x0r - x2r;
				a[j2 + 1] = x0i - x2i;
				a[j1] = x1r - x3i;
				a[j1 + 1] = x1i + x3r;
				a[j3] = x1r + x3i;
				a[j3 + 1] = x1i - x3r;
			}
		} else {
			for (j = 0; j < l; j += 2) {
				j1 = j + l;
				x0r = a[j] - a[j1];
				x0i = a[j + 1] - a[j1 + 1];
				a[j] += a[j1];
				a[j + 1] += a[j1 + 1];
				a[j1] = x0r;
				a[j1 + 1] = x0i;
			}
		}
	}

	private void cftbsub(int n, double[] a, double[] w){
		int j, j1, j2, j3, l;
		double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		l = 2;
		if (n > 8) {
			cft1st(n, a, w);
			l = 8;
			while ((l << 2) < n) {
				cftmdl(n, l, a, w);
				l <<= 2;
			}
		}
		if ((l << 2) == n) {
			for (j = 0; j < l; j += 2) {
				j1 = j + l;
				j2 = j1 + l;
				j3 = j2 + l;
				x0r = a[j] + a[j1];
				x0i = -a[j + 1] - a[j1 + 1];
				x1r = a[j] - a[j1];
				x1i = -a[j + 1] + a[j1 + 1];
				x2r = a[j2] + a[j3];
				x2i = a[j2 + 1] + a[j3 + 1];
				x3r = a[j2] - a[j3];
				x3i = a[j2 + 1] - a[j3 + 1];
				a[j] = x0r + x2r;
				a[j + 1] = x0i - x2i;
				a[j2] = x0r - x2r;
				a[j2 + 1] = x0i + x2i;
				a[j1] = x1r - x3i;
				a[j1 + 1] = x1i - x3r;
				a[j3] = x1r + x3i;
				a[j3 + 1] = x1i + x3r;
			}
		} else {
			for (j = 0; j < l; j += 2) {
				j1 = j + l;
				x0r = a[j] - a[j1];
				x0i = -a[j + 1] + a[j1 + 1];
				a[j] += a[j1];
				a[j + 1] = -a[j + 1] - a[j1 + 1];
				a[j1] = x0r;
				a[j1 + 1] = x0i;
			}
		}
	}

	private void cft1st(int n, double[] a, double[] w){
		int j, k1, k2;
		double wk1r, wk1i, wk2r, wk2i, wk3r, wk3i;
		double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		x0r = a[0] + a[2];
		x0i = a[1] + a[3];
		x1r = a[0] - a[2];
		x1i = a[1] - a[3];
		x2r = a[4] + a[6];
		x2i = a[5] + a[7];
		x3r = a[4] - a[6];
		x3i = a[5] - a[7];
		a[0] = x0r + x2r;
		a[1] = x0i + x2i;
		a[4] = x0r - x2r;
		a[5] = x0i - x2i;
		a[2] = x1r - x3i;
		a[3] = x1i + x3r;
		a[6] = x1r + x3i;
		a[7] = x1i - x3r;
		wk1r = w[2];
		x0r = a[8] + a[10];
		x0i = a[9] + a[11];
		x1r = a[8] - a[10];
		x1i = a[9] - a[11];
		x2r = a[12] + a[14];
		x2i = a[13] + a[15];
		x3r = a[12] - a[14];
		x3i = a[13] - a[15];
		a[8] = x0r + x2r;
		a[9] = x0i + x2i;
		a[12] = x2i - x0i;
		a[13] = x0r - x2r;
		x0r = x1r - x3i;
		x0i = x1i + x3r;
		a[10] = wk1r * (x0r - x0i);
		a[11] = wk1r * (x0r + x0i);
		x0r = x3i + x1r;
		x0i = x3r - x1i;
		a[14] = wk1r * (x0i - x0r);
		a[15] = wk1r * (x0i + x0r);
		k1 = 0;
		for (j = 16; j < n; j += 16) {
			k1 += 2;
			k2 = 2 * k1;
			wk2r = w[k1];
			wk2i = w[k1 + 1];
			wk1r = w[k2];
			wk1i = w[k2 + 1];
			wk3r = wk1r - 2 * wk2i * wk1i;
			wk3i = 2 * wk2i * wk1r - wk1i;
			x0r = a[j] + a[j + 2];
			x0i = a[j + 1] + a[j + 3];
			x1r = a[j] - a[j + 2];
			x1i = a[j + 1] - a[j + 3];
			x2r = a[j + 4] + a[j + 6];
			x2i = a[j + 5] + a[j + 7];
			x3r = a[j + 4] - a[j + 6];
			x3i = a[j + 5] - a[j + 7];
			a[j] = x0r + x2r;
			a[j + 1] = x0i + x2i;
			x0r -= x2r;
			x0i -= x2i;
			a[j + 4] = wk2r * x0r - wk2i * x0i;
			a[j + 5] = wk2r * x0i + wk2i * x0r;
			x0r = x1r - x3i;
			x0i = x1i + x3r;
			a[j + 2] = wk1r * x0r - wk1i * x0i;
			a[j + 3] = wk1r * x0i + wk1i * x0r;
			x0r = x1r + x3i;
			x0i = x1i - x3r;
			a[j + 6] = wk3r * x0r - wk3i * x0i;
			a[j + 7] = wk3r * x0i + wk3i * x0r;
			wk1r = w[k2 + 2];
			wk1i = w[k2 + 3];
			wk3r = wk1r - 2 * wk2r * wk1i;
			wk3i = 2 * wk2r * wk1r - wk1i;
			x0r = a[j + 8] + a[j + 10];
			x0i = a[j + 9] + a[j + 11];
			x1r = a[j + 8] - a[j + 10];
			x1i = a[j + 9] - a[j + 11];
			x2r = a[j + 12] + a[j + 14];
			x2i = a[j + 13] + a[j + 15];
			x3r = a[j + 12] - a[j + 14];
			x3i = a[j + 13] - a[j + 15];
			a[j + 8] = x0r + x2r;
			a[j + 9] = x0i + x2i;
			x0r -= x2r;
			x0i -= x2i;
			a[j + 12] = -wk2i * x0r - wk2r * x0i;
			a[j + 13] = -wk2i * x0i + wk2r * x0r;
			x0r = x1r - x3i;
			x0i = x1i + x3r;
			a[j + 10] = wk1r * x0r - wk1i * x0i;
			a[j + 11] = wk1r * x0i + wk1i * x0r;
			x0r = x1r + x3i;
			x0i = x1i - x3r;
			a[j + 14] = wk3r * x0r - wk3i * x0i;
			a[j + 15] = wk3r * x0i + wk3i * x0r;
		}
	}

	private void cftmdl(int n, int l, double[] a, double[] w){
		int j, j1, j2, j3, k, k1, k2, m, m2;
		double wk1r, wk1i, wk2r, wk2i, wk3r, wk3i;
		double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		m = l << 2;
		for (j = 0; j < l; j += 2) {
			j1 = j + l;
			j2 = j1 + l;
			j3 = j2 + l;
			x0r = a[j] + a[j1];
			x0i = a[j + 1] + a[j1 + 1];
			x1r = a[j] - a[j1];
			x1i = a[j + 1] - a[j1 + 1];
			x2r = a[j2] + a[j3];
			x2i = a[j2 + 1] + a[j3 + 1];
			x3r = a[j2] - a[j3];
			x3i = a[j2 + 1] - a[j3 + 1];
			a[j] = x0r + x2r;
			a[j + 1] = x0i + x2i;
			a[j2] = x0r - x2r;
			a[j2 + 1] = x0i - x2i;
			a[j1] = x1r - x3i;
			a[j1 + 1] = x1i + x3r;
			a[j3] = x1r + x3i;
			a[j3 + 1] = x1i - x3r;
		}
		wk1r = w[2];
		for (j = m; j < l + m; j += 2) {
			j1 = j + l;
			j2 = j1 + l;
			j3 = j2 + l;
			x0r = a[j] + a[j1];
			x0i = a[j + 1] + a[j1 + 1];
			x1r = a[j] - a[j1];
			x1i = a[j + 1] - a[j1 + 1];
			x2r = a[j2] + a[j3];
			x2i = a[j2 + 1] + a[j3 + 1];
			x3r = a[j2] - a[j3];
			x3i = a[j2 + 1] - a[j3 + 1];
			a[j] = x0r + x2r;
			a[j + 1] = x0i + x2i;
			a[j2] = x2i - x0i;
			a[j2 + 1] = x0r - x2r;
			x0r = x1r - x3i;
			x0i = x1i + x3r;
			a[j1] = wk1r * (x0r - x0i);
			a[j1 + 1] = wk1r * (x0r + x0i);
			x0r = x3i + x1r;
			x0i = x3r - x1i;
			a[j3] = wk1r * (x0i - x0r);
			a[j3 + 1] = wk1r * (x0i + x0r);
		}
		k1 = 0;
		m2 = 2 * m;
		for (k = m2; k < n; k += m2) {
			k1 += 2;
			k2 = 2 * k1;
			wk2r = w[k1];
			wk2i = w[k1 + 1];
			wk1r = w[k2];
			wk1i = w[k2 + 1];
			wk3r = wk1r - 2 * wk2i * wk1i;
			wk3i = 2 * wk2i * wk1r - wk1i;
			for (j = k; j < l + k; j += 2) {
				j1 = j + l;
				j2 = j1 + l;
				j3 = j2 + l;
				x0r = a[j] + a[j1];
				x0i = a[j + 1] + a[j1 + 1];
				x1r = a[j] - a[j1];
				x1i = a[j + 1] - a[j1 + 1];
				x2r = a[j2] + a[j3];
				x2i = a[j2 + 1] + a[j3 + 1];
				x3r = a[j2] - a[j3];
				x3i = a[j2 + 1] - a[j3 + 1];
				a[j] = x0r + x2r;
				a[j + 1] = x0i + x2i;
				x0r -= x2r;
				x0i -= x2i;
				a[j2] = wk2r * x0r - wk2i * x0i;
				a[j2 + 1] = wk2r * x0i + wk2i * x0r;
				x0r = x1r - x3i;
				x0i = x1i + x3r;
				a[j1] = wk1r * x0r - wk1i * x0i;
				a[j1 + 1] = wk1r * x0i + wk1i * x0r;
				x0r = x1r + x3i;
				x0i = x1i - x3r;
				a[j3] = wk3r * x0r - wk3i * x0i;
				a[j3 + 1] = wk3r * x0i + wk3i * x0r;
			}
			wk1r = w[k2 + 2];
			wk1i = w[k2 + 3];
			wk3r = wk1r - 2 * wk2r * wk1i;
			wk3i = 2 * wk2r * wk1r - wk1i;
			for (j = k + m; j < l + (k + m); j += 2) {
				j1 = j + l;
				j2 = j1 + l;
				j3 = j2 + l;
				x0r = a[j] + a[j1];
				x0i = a[j + 1] + a[j1 + 1];
				x1r = a[j] - a[j1];
				x1i = a[j + 1] - a[j1 + 1];
				x2r = a[j2] + a[j3];
				x2i = a[j2 + 1] + a[j3 + 1];
				x3r = a[j2] - a[j3];
				x3i = a[j2 + 1] - a[j3 + 1];
				a[j] = x0r + x2r;
				a[j + 1] = x0i + x2i;
				x0r -= x2r;
				x0i -= x2i;
				a[j2] = -wk2i * x0r - wk2r * x0i;
				a[j2 + 1] = -wk2i * x0i + wk2r * x0r;
				x0r = x1r - x3i;
				x0i = x1i + x3r;
				a[j1] = wk1r * x0r - wk1i * x0i;
				a[j1 + 1] = wk1r * x0i + wk1i * x0r;
				x0r = x1r + x3i;
				x0i = x1i - x3r;
				a[j3] = wk3r * x0r - wk3i * x0i;
				a[j3 + 1] = wk3r * x0i + wk3i * x0r;
			}
		}
	}

	private void rftfsub(int n, double[] a, int nc, double[] c, int nw){
		int j, k, kk, ks, m;
		double wkr, wki, xr, xi, yr, yi;

		m = n >> 1;
		ks = 2 * nc / m;
		kk = 0;
		for (j = 2; j < m; j += 2) {
			k = n - j;
			kk += ks;
			wkr = 0.5 - c[nc - kk + nw];
			wki = c[kk + nw];
			xr = a[j] - a[k];
			xi = a[j + 1] + a[k + 1];
			yr = wkr * xr - wki * xi;
			yi = wkr * xi + wki * xr;
			a[j] -= yr;
			a[j + 1] -= yi;
			a[k] += yr;
			a[k + 1] -= yi;
		}
	}

	private void rftbsub(int n, double[] a, int nc, double[] c, int nw){
		int j, k, kk, ks, m;
		double wkr, wki, xr, xi, yr, yi;

		a[1] = -a[1];
		m = n >> 1;
		ks = 2 * nc / m;
		kk = 0;
		for (j = 2; j < m; j += 2) {
			k = n - j;
			kk += ks;
			wkr = 0.5 - c[nc - kk + nw];
			wki = c[kk + nw];
			xr = a[j] - a[k];
			xi = a[j + 1] + a[k + 1];
			yr = wkr * xr + wki * xi;
			yi = wkr * xi - wki * xr;
			a[j] -= yr;
			a[j + 1] = yi - a[j + 1];
			a[k] += yr;
			a[k + 1] = yi - a[k + 1];
		}
		a[m + 1] = -a[m + 1];
	}

	private void dctsub(int n, double[] a, int nc, double[] c, int nw){
		int j, k, kk, ks, m;
		double wkr, wki, xr;

		m = n >> 1;
		ks = nc / n;
		kk = 0;
		for (j = 1; j < m; j++) {
			k = n - j;
			kk += ks;
			wkr = c[kk + nw] - c[nc - kk + nw];
			wki = c[kk + nw] + c[nc - kk + nw];
			xr = wki * a[j] - wkr * a[k];
			a[j] = wkr * a[j] + wki * a[k];
			a[k] = xr;
		}
		a[m] *= c[0 + nw];
	}

	private void dstsub(int n, double[] a, int nc, double[] c, int nw){
		int j, k, kk, ks, m;
		double wkr, wki, xr;

		m = n >> 1;
		ks = nc / n;
		kk = 0;
		for (j = 1; j < m; j++) {
			k = n - j;
			kk += ks;
			wkr = c[kk + nw] - c[nc - kk + nw];
			wki = c[kk + nw] + c[nc - kk + nw];
			xr = wki * a[k] - wkr * a[j];
			a[k] = wkr * a[k] + wki * a[j];
			a[j] = xr;
		}
		a[m] *= c[0 + nw];
	}
};