package com.chenlb.mmseg4j;

import java.util.List;

/**
 * 
 * 
 * @author chenlb 2009-3-16 下午09:15:30
 */
public abstract class Seg {

	protected Dictionary dic;
	
	public Seg(Dictionary dic) {
		super();
		this.dic = dic;
	}

	/**
	 * 输出 chunks, 调试用.
	 */
	protected void printChunk(List<Chunk> chunks) {
		for(Chunk ck : chunks) {
			System.out.println(ck+" -> "+ck.toFactorString());
		}
	}
	
	/**
	 * 查找chs[offset]后面的 len个char是否为词.
	 * @return 返回chs[offset]字符结点下的词尾索引号,没找到返回 -1.
	 * @deprecated 使用 {@link #search(CharNode, char[], int, int)}
	 */
	protected int search(char[] chs, int offset, int len) {
		if(len == 0) {
			return -1;
		}
		CharNode cn = dic.head(chs[offset]);
		/*if(cn == null) {
			return -1;
		}
		char[] subChs = new char[len];
		System.arraycopy(chs, offset+1, subChs, 0, len);
		return dic.search(cn, subChs);*/
		return search(cn, chs, offset, len, new char[1][], 0);
	}
	
	/**
	 * 从 cn 结果下找chs[offset]后面tailLenlen个字符.
	 * @param cks 复制到 cks[ckIdx] 中, 它可能是一个词, 生成 Chunk 时就不用在 System.arraycopy 了
	 * @return 返回chs[offset]字符结点下的词尾索引号,没找到返回 -1.
	 * @author chenlb 2009-3-29 下午01:31:16
	 */
	protected int search(CharNode cn, char[] chs, int offset, int tailLen, char[][] cks, int ckIdx) {
		if(tailLen == 0) {
			if(offset < chs.length) {
				cks[ckIdx] = new char[1];
				cks[ckIdx][0] = chs[offset];
			} else {
				cks[ckIdx] = null;
			}
			return -1;
		}
		
		if(cn == null) {
			if(offset < chs.length) {
				cks[ckIdx] = new char[1];
				cks[ckIdx][0] = chs[offset];
			} else {
				cks[ckIdx] = null;
			}
			return -1;
		}
		char[] subChs = new char[tailLen+1];
		System.arraycopy(chs, offset, subChs, 0, tailLen+1);
		cks[ckIdx] = subChs;
		return dic.search(cn, subChs);
	}
	/**
	 * 结合与词库长度给出可用的词的最大长度
	 * @param chs 待分词的句子
	 * @param offset 词在 chs 的开始位置
	 * @deprecated 使用 {@link #getLens(char[], int, int[], int)}
	 */
	protected int maxLen(char[] chs, int offset) {
		if(offset >= chs.length) {
			return 0;
		}
		CharNode cn = dic.head(chs[offset]);
		if(cn == null) {
			return 0;
		}
		return Math.min(cn.getMaxLen(), chs.length-offset-1);
	}
	
	/**
	 * 取得所有chs[offset]字符开始的不同词长<br/>
	 * 同时计算出可行的最大长度, 保存在maxAvailableLen[maxLenIdx].
	 * @param chs 待分词的句子
	 * @param offset 词在 chs 的开始位置
	 * @param maxAvailableLen 结合与词库长度给出可用的词的最大长度的保存空间
	 * @param maxLenIdx maxAvailableLen 的索引号
	 * @return 至少一个 {0}
	 * @deprecated 使用 {@link #getLens(CharNode[], int, char[], int, int[], int)}
	 */
	protected int[] getLens(char[] chs, int offset, int[] maxAvailableLen, int maxLenIdx) {
		if(offset >= chs.length) {
			maxAvailableLen[maxLenIdx] = 0;
			return new int[] {0};
		}
		CharNode cn = dic.head(chs[offset]);
		if(cn == null) {
			maxAvailableLen[maxLenIdx] = 0;
			return new int[] {0};
		}
		maxAvailableLen[maxLenIdx] = Math.min(cn.getMaxLen(), chs.length-offset-1);
		return cn.getLens();
	}
	
	/**
	 * 取得所有chs[offset]字符开始的不同词长, 并把chs[offest]的字符结点保存在cns[cnIdx]中<br/>
	 * 同时计算出可行的最大长度, 保存在maxAvailableLen[maxLenIdx].
	 * @param chs 待分词的句子
	 * @param offset 词在 chs 的开始位置
	 * @param maxAvailableLen 结合与词库长度给出可用的词的最大长度的保存空间
	 * @param maxLenIdx maxAvailableLen 的索引号
	 * @return 至少一个 {0}
	 * @author chenlb 2009-3-29 下午01:33:01
	 */
	protected int[] getLens(CharNode[] cns, int cnIdx, char[] chs, int offset, int[] maxAvailableLen, int maxLenIdx) {
		if(offset >= chs.length) {
			maxAvailableLen[maxLenIdx] = 0;
			cns[cnIdx] = null;
			return new int[] {0};
		}
		CharNode cn = dic.head(chs[offset]);
		if(cn == null) {
			maxAvailableLen[maxLenIdx] = 0;
			cns[cnIdx] = null;
			return new int[] {0};
		}
		maxAvailableLen[maxLenIdx] = Math.min(cn.getMaxLen(), chs.length-offset-1);
		cns[cnIdx] = cn;
		return cn.getLens();
	}
	
	public abstract Chunk seg(Sentence sen);
}
