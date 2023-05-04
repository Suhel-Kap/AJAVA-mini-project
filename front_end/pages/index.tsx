import {FormEventHandler, useState} from "react";
import Head from "next/head";

export default function Home() {
    const [form, setForm] = useState({
        email: "",
        password: ""
    })

    const handleSubmit = async (e: any) => {
        e.preventDefault()
        console.log(form)
    }

    return (
        <>
            <Head>
                <title>Login</title>
            </Head>
            <div className="box-center">
                <form onSubmit={handleSubmit}>
                    <div className="login-card">
                        <label for="email" className="input-label">
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
                        <button type="submit" className="submit-button">Sign In</button>
                        <p className="signup-text">Not signed up? <a href="/signup">Sign Up</a></p>
                    </div>
                </form>
            </div>
        </>
    )
}
