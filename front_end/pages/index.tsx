import {FormEventHandler, useState} from "react";
import Head from "next/head";
import {useRouter} from "next/router";
import axios from "axios";
import Alert from "@mui/material/Alert";

export default function Home() {
    const [showAlert, setShowAlert] = useState<boolean>(false)
    const [alertMessage, setAlertMessage] = useState("")
    const router = useRouter()
    const [form, setForm] = useState({
        email: "",
        password: ""
    })

    const handleSubmit = async (e: any) => {
        e.preventDefault()
        let res = null
        try {
            res = await axios.post("http://localhost:8080/Notes/SignIn", {
                email: form.email,
                password: form.password,
            }, {
                headers: {
                    "Content-Type": "application/json"
                }
            })
            sessionStorage.setItem("email", form.email)
            sessionStorage.setItem("loggedIn", res.data.loggedIn)
            router.push("/notes")
        } catch (e) {
            if (e.response.status === 404) {
                console.log(e.response.status === 404)
                setShowAlert(true)
                setAlertMessage("User does not exist")
            }
            if (e.response.status === 403) {
                setShowAlert(true)
                setAlertMessage("Username or password incorrect")
            }
        }
    }

    return (
        <>
            <Head>
                <title>Login</title>
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
                        {showAlert && <Alert style={{margin: "8px 5px"}} severity="error">{alertMessage}</Alert>}
                        <button type="submit" className="submit-button">Sign In</button>
                        <p className="signup-text">Not signed up? <a href="/signup">Sign Up</a></p>
                    </div>
                </form>
            </div>
        </>
    )
}
