import React, {useEffect, useState} from "react";
import HighlightIcon from "@mui/icons-material/Highlight";
import axios from "axios";
import {useRouter} from "next/router";

function Header() {
    const router = useRouter()
    const [email, setEmail] = useState<string>()

    const handleLogout = async () => {
        try {
            sessionStorage.clear()
            const res = await axios.delete("http://localhost:8080/Notes/SignIn")
            router.push("/")
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        if(window){
            const email = sessionStorage?.getItem("email")
            setEmail(email)
        }
    }, [])

    return (
        <header>
            <div className="header">
                <h1>
                    <HighlightIcon/>
                    Keeper
                </h1>
                <div>
                    <p style={{color: "white"}}>{email}</p>
                    <button onClick={handleLogout}>
                        Sign Out
                    </button>
                </div>
            </div>
        </header>
    );
}

export default Header;
