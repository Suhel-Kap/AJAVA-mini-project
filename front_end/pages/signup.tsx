import {FormEventHandler, useState} from "react";
import Head from "next/head";
import Alert from '@mui/material/Alert';
import axios from "axios";
import {useRouter} from "next/router";

export default function Home() {
    const [form, setForm] = useState({
        email: "",
        password: "",
        confirmPassword: ""
    })
    const [pwMatched, setPwMatched] = useState<boolean>(true)
    const [alertMessage, setAlertMessage] = useState("")
    const router = useRouter()

    const handleSubmit = async (e: any) => {
        e.preventDefault()
        console.log(form)
        if(form.password !== form.confirmPassword){
            setPwMatched(false)
            setAlertMessage("Passwords do not match!")
            return
        } else if(form.password.length < 6){
            setPwMatched(false)
            setAlertMessage("Password length can not be less than 6")
            return
        } else {
            setPwMatched(true)
            setAlertMessage("")
        }
        let res = null
        try {
            res = await axios.post("http://localhost:8080/Notes/SignUp", {
                email: form.email,
                password: form.password,
                confirmPassword: form.confirmPassword
            }, {
                headers: {
                    "Content-Type": "application/json"
                }
            })
            sessionStorage.setItem("email", form.email)
            sessionStorage.setItem("loggedIn", res.data.loggedIn)
            router.push("/notes")
        } catch (e){
            console.error(e)
            if(e.response.status === 503){
                setPwMatched(false)
                setAlertMessage("User already exists")
            }
        }
    }

    return (
        <>
            <Head>
                <title>Sign Up</title>
            </Head>
            <div className="box-center">
                <form onSubmit={handleSubmit}>
                    <div className="login-card">
                        <label htmlFor="email" className="input-label">
                            Email <span style={{color: "red"}}>*</span>
                        </label>
                        <input className="input-field" name="email" type="email" placeholder="john@doe.com" required
                               value={form.email}
                               onChange={(e) => setForm({...form, email: e.target.value})}/>
                        <label htmlFor="password" className="input-label">
                            Password <span style={{color: "red"}}>*</span>
                        </label>
                        <input className="input-field" name="password" type="password" placeholder="Enter your password"
                               required
                               value={form.password}
                               onChange={(e) => setForm({...form, password: e.target.value})}/>
                        <label htmlFor="confirmPassword" className="input-label">
                            Confirm Password <span style={{color: "red"}}>*</span>
                        </label>
                        <input className="input-field" name="confirmPassword" type="password" placeholder="Re-enter your password"
                               required
                               value={form.confirmPassword}
                               onChange={(e) => setForm({...form, confirmPassword: e.target.value})}/>
                        {!pwMatched && <Alert style={{margin: "8px 5px"}} severity="error">{alertMessage}</Alert>}
                        <button type="submit" className="submit-button">Sign Up</button>
                        <p className="signup-text">Already signed up? <a href="/">Sign In</a></p>
                    </div>
                </form>
            </div>
        </>
    )
}
