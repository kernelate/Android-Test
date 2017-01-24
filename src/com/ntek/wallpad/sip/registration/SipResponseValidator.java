package com.ntek.wallpad.sip.registration;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public class SipResponseValidator {
	
	/*private final static int RESPONSES_CLIENT_FAILURE = 400;
	private final static int RESPONSES_SERVER_FAILURE = 500;
	private final static int RESPONSES_GLOBAL_FAILURE = 600;*/
	
	@SuppressLint("UseSparseArrays")
	private final static Map<Integer, String[]> responses = new HashMap<Integer, String[]>();
	
	static {
		responses.put(400, new String[]{"Bad Request", "The request could not be understood due to malformed syntax."});
		responses.put(401, new String[]{"Unauthorized", "The request requires user authentication. This response is issued by UASs and registrars."});
		responses.put(402, new String[]{"Payment Required", "(Reserved for future use)."});
		responses.put(403, new String[]{"Forbidden", "The server understood the request, but is refusing to fulfill it."});
		responses.put(404, new String[]{"Not Found", "The server has definitive information that the user does not exist at the (User not found)."});
		responses.put(405, new String[]{"Method Not Allowed", "The method specified in the Request-Line is understood, but not allowed ."});
		responses.put(406, new String[]{"Not Acceptable", "The resource is only capable of generating responses with unacceptable content."});
		responses.put(407, new String[]{"Proxy Authentication Required", "The request requires user authentication."});
		responses.put(408, new String[]{"Request Timeout", "Couldn��t find the user in time."});
		responses.put(409, new String[]{"Conflict", "User already registered (deprecated)"});
		responses.put(410, new String[]{"Gone", "The user existed once, but is not available here any more."});
		responses.put(411, new String[]{"Length Required", "The server will not accept the request without a valid content length (deprecated)."});
		responses.put(413, new String[]{"Request Entity Too Large", "Request body too large."});
		responses.put(414, new String[]{"Request URI Too Long", "Server refuses to service the request, the Req-URI is longer than the server can interpret."});
		responses.put(415, new String[]{"Unsupported Media Type", "Request body is in a non supported  format."});
		responses.put(416, new String[]{"Unsupported URI Scheme", "Request-URI is unknown to the server."});
		responses.put(417, new String[]{"Uknown Resource-Priority", "There was a resource-priority option tag, but no Resource-Priority header."});
		responses.put(420, new String[]{"Bad Extension", "Bad SIP Protocol Extension used, not understood by the server."});
		responses.put(421, new String[]{"Extension Required", "The server needs a specific extension not listed in the Supported header."});
		responses.put(422, new String[]{"Session Interval Too Small", "The request contains a Session-Expires header field with duration below the minimum."});
		responses.put(423, new String[]{"Interval Too Brief", "Expiration time of the resource is too short."});
		responses.put(424, new String[]{"Bad Location Information", "The request��s location content was malformed or otherwise unsatisfactory."});
		responses.put(428, new String[]{"Use Identity Header", "The server policy requires an Identity header, and one has not been provided."});
		responses.put(429, new String[]{"Provide Referrer Identity", "The server did not receive a valid Referred-By token on the request."});
		responses.put(430, new String[]{"Flow Failed", "A specific flow to a user agent has failed, although other flows may succeed."});
		responses.put(433, new String[]{"Anonymity Disallowed", "The request has been rejected because it was anonymous."});
		responses.put(436, new String[]{"Bad Identity Info", "The request has an Identity-Info header and the   URI scheme contained cannot be de-referenced."});
		responses.put(437, new String[]{"Unsupported Certificate", "The server was unable to validate a certificate for the domain that signed the request."});
		responses.put(438, new String[]{"Invalid Identity Header", "Server obtained a valid certificate used to sign a request, was unable to verify the signature."});
		responses.put(439, new String[]{"First Hop Lacks Outbound Support", "The first outbound proxy doesn��t support ��outbound�� feature."});
		responses.put(470, new String[]{"Consent Needed", "The source of the request did not have the permission of the recipient to make such a request."});
		responses.put(480, new String[]{"Temporarily Unavailable", "Callee currently unavailable."});
		responses.put(481, new String[]{"Call/Transaction Does Not Exist", "Server received a request that does not match any dialog or transaction."});
		responses.put(482, new String[]{"Loop Detected", "Server has detected a loop."});
		responses.put(483, new String[]{"Too Many Hops", "Max-Forwards header has reached the value ��0��."});
		responses.put(484, new String[]{"Address Incomplete", "Request-URI incomplete."});
		responses.put(485, new String[]{"Ambiguous", "Request-URI is ambiguous."});
		responses.put(486, new String[]{"Busy Here", "Callee is busy."});
		responses.put(487, new String[]{"Request Terminated", "Request has terminated by bye or cancel."});
		responses.put(488, new String[]{"Not Acceptable Here", "Some aspects of the session description of the Request-URI are not acceptable."});
		responses.put(489, new String[]{"Bad Event", "The server did not understand an event package specified in an Event header field."});
		responses.put(491, new String[]{"Request Pending", "Server has some pending request from the same dialog."});
		responses.put(493, new String[]{"Undecipherable", "UndecipherableRequest contains an encrypted MIME body, which recipient can not decrypt."});
		responses.put(494, new String[]{"Security Agreement Required", "The server has received a request that requires a negotiated security mechanism."});
		responses.put(500, new String[]{"Server Internal Error", "The server could not fulfill the request due to some unexpected condition."});
		responses.put(501, new String[]{"Not Implemented", "The SIP request method is not implemented here."});
		responses.put(502, new String[]{"Bad Gateway", "The server, received an invalid response from a downstream server while trying  to fulfill a request."});
		responses.put(503, new String[]{"Service Unavailable", "The server is in maintenance or is temporarily overloaded and cannot process the request."});
		responses.put(504, new String[]{"Server Time-out", "The server tried to access another server while trying  to process a request, no timely response."});
		responses.put(505, new String[]{"Version Not Supported", "The SIP protocol version in the request is not supported by the server."});
		responses.put(513, new String[]{"Message Too Large", "The request message length is longer than the server can process."});
		responses.put(580, new String[]{"Precondition Failure", "The server is unable or unwilling to meet some constraints specified in the offer."});
		responses.put(600, new String[]{"Busy Everywhere", "All possible destinations are busy."});
		responses.put(603, new String[]{"Decline", "Destination cannot/doen��t wish to participate in the call,  no alternative destinations."});
		responses.put(604, new String[]{"Does Not Exist Anywhere", "The server has authoritative information that the requested user does not exist anywhere."});
		responses.put(606, new String[]{"Not Acceptable", "The user��s agent was contacted successfully but some aspects of the session description were not acceptable."});
		// special case
		//responses.put(903, new String[]{"Unexpect disconnection", "Session has unexpectedly disconnected, please try to register again"});
	}
	
	private SipResponseValidator() {
	}

	interface SipResponseObject {
		boolean belongToErrorCodes();
		String getPhrase();
		String getMessage();
		int getCode();
	}
	
	public static SipResponseObject validate(final short sipcode) {
		return new SipResponseObject() {
			@Override
			public boolean belongToErrorCodes() {
				return responses.containsKey(Integer.valueOf(sipcode)); 
			}
			@Override
			public String getPhrase() {
				String[] m = responses.get(Integer.valueOf(sipcode));
				return (m!=null?m[0]:"");  
			}
			@Override
			public String getMessage() {
				String[] m = responses.get(Integer.valueOf(sipcode));
				return (m!=null?m[1]:"");  
			}			
			@Override
			public int getCode() 
			{ return sipcode; }
		};
	}
}
