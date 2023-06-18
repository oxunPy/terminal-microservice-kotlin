package com.example.common.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.Serializable
import java.util.*
import java.util.function.Function

class JwtTokenUtil(private var jwtValidityPeriod: Long, private var jwtSecretKey: String) : Serializable {

    fun doGenerateToken(claims: Map<String, Any>, issuer: String?, subject: String, audience: String?, id: String?): String? {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setSubject(subject)
            .setAudience(audience)
            .setId(id)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + this.jwtValidityPeriod.times(1000L)))
            .signWith(SignatureAlgorithm.HS512, jwtSecretKey).compact()
    }

    fun getAllClaimsFromToken(token: String): Claims? {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).body
    }

    private fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims?, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    fun generateToken(subject: String): String? {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, null, subject, null, null)
    }

    fun generateToken(subject: String, id: String): String? {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, null, subject, null, id)
    }

    fun getIssuerFromToken(token: String?): String? {
        return getClaimFromToken(token!!) { obj: Claims? -> obj!!.issuer }
    }

    fun getSubjectFromToken(token: String?): String? {
        return getClaimFromToken(token!!) {obj: Claims? -> obj!!.subject}
    }

    fun getAudienceFromToken(token: String?): String? {
        return getClaimFromToken(token!!) {obj: Claims? -> obj!!.audience}
    }

    fun getExpirationFromToken(token: String?): Date? {
        return getClaimFromToken(token!!) {obj: Claims? -> obj!!.expiration}
    }

    fun getIssuedAtFromToken(token: String?): Date? {
        return getClaimFromToken(token!!) {obj: Claims? -> obj!!.issuedAt}
    }

    fun validate(token: String?): Boolean{
        try {
            if (token != null) {
                getAllClaimsFromToken(token)
                return true
            }
        } catch (ex: Exception){
            throw ex
        }
        return false
    }


}